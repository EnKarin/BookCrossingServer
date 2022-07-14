package io.github.enkarin.bookcrossing.user.service;

import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.exception.*;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import io.github.enkarin.bookcrossing.user.dto.UserProfileDto;
import io.github.enkarin.bookcrossing.user.dto.UserPublicProfileDto;
import io.github.enkarin.bookcrossing.user.dto.UserPutProfileDto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class UserServiceTest extends BookCrossingBaseTests {
    private static final int GM_TIME_ZERO = 0;

    private UserDto createAndSaveUser() {
        final UserDto user = userService.saveUser(TestDataProvider.buildAlex());
        usersId.add(user.getUserId());
        return user;
    }

    @Test
    void saveUserCorrectUserTest() {
        assertThat(userService.findById(createAndSaveUser().getUserId(), GM_TIME_ZERO)).isNotNull();
    }

    @Test
    void saveUserNonConfirmedPasswordTest() {
        assertThatThrownBy(() -> userService.saveUser(TestDataProvider.buildNonConfirmedPasswordUser()))
                .isInstanceOf(PasswordsDontMatchException.class)
                .hasMessage("Пароли не совпадают");
    }

    @Test
    void saveUserDuplicatedUserTest() {
        createAndSaveUser();
        assertThatThrownBy(() -> userService.saveUser(TestDataProvider.buildAlex()))
                .isInstanceOf(LoginFailedException.class)
                .hasMessage("login: Пользователь с таким логином уже существует");
    }

    @Test
    void saveUserDuplicatedEmailTest() {
        createAndSaveUser();
        assertThatThrownBy(() -> userService.saveUser(TestDataProvider.buildUserWithAlexEmail()))
                .isInstanceOf(EmailFailedException.class)
                .hasMessage("email: Пользователь с таким почтовым адресом уже существует");
    }

    @Test
    void confirmMailWrongTokenTest() {
        assertThatThrownBy(() -> userService.confirmMail("wrong token"))
                .isInstanceOf(TokenNotFoundException.class)
                .hasMessage("Токен не найден");
    }

    @Test
    void findByLoginCorrectTest() {
        assertThat(userService.findByLogin(createAndSaveUser().getLogin())).isNotNull();
    }

    @Test
    void findByLoginNonExistingUserTest() {
        assertThatThrownBy(() -> userService.findByLogin("Non existing login"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void getProfileTest() {
        final UserDto userDTO = createAndSaveUser();
        final UserProfileDto userProfileDto = userService.getProfile(userDTO.getLogin());
        checkUserDTOAndUserProfileDTO(userDTO, userProfileDto);
    }

    @Test
    void findAllIsEmptyListTest() {
        assertThat(userService.findAllUsers(GM_TIME_ZERO)).isEmpty();
    }

    @Test
    void findAllTest() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
                .map(u -> userService.saveUser(u))
                .collect(Collectors.toList());
        users.forEach(u -> usersId.add(u.getUserId()));

        final var foundUsers = userService.findAllUsers(GM_TIME_ZERO);
        assertThat(foundUsers).hasSize(users.size());

        final var usersIterator = users.iterator();
        final var foundIterator = foundUsers.iterator();
        while (usersIterator.hasNext() && foundIterator.hasNext()) {
            checkUserDTOAndUserPublicPublicProfileDTO(usersIterator.next(), foundIterator.next());
        }
    }

    @Test
    void deleteUserTest() {
        final UserDto user = userService.saveUser(TestDataProvider.buildAlex());

        userService.deleteUser(user.getUserId());
        assertThatThrownBy(() -> userService.findById(user.getUserId(), GM_TIME_ZERO))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void putUserInfoTest() {
        final UserDto user = createAndSaveUser();

        final UserPutProfileDto putProfile = UserPutProfileDto.create(
                "Mike",
                "123456",
                "654321",
                "654321",
                "Moscow"
        );

        userService.putUserInfo(putProfile, user.getLogin());
        final UserPublicProfileDto newProfile = userService.findById(user.getUserId(), GM_TIME_ZERO);
        assertThat(putProfile.getName()).isEqualTo(newProfile.getName());
        assertThat(putProfile.getCity()).isEqualTo(newProfile.getCity());
    }

    @Test
    void putUserInfoNonConfirmedPasswordTest() {
        final UserDto user = createAndSaveUser();

        final UserPutProfileDto putProfile = UserPutProfileDto.create(
                "Mike",
                "123456",
                "654321",
                "123456",
                "Moscow"
        );

        assertThatThrownBy(() -> userService.putUserInfo(putProfile, user.getLogin()))
                .isInstanceOf(PasswordsDontMatchException.class)
                .hasMessage("Пароли не совпадают");
    }

    @Test
    void putUserInfoWrongOldPasswordTest() {
        final UserDto user = createAndSaveUser();

        final UserPutProfileDto putProfile = UserPutProfileDto.create(
                "Mike",
                "wrong password",
                "654321",
                "654321",
                "Moscow"
        );

        assertThatThrownBy(() -> userService.putUserInfo(putProfile, user.getLogin()))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Некорректный пароль");
    }

    private static void checkUserDTOAndUserPublicPublicProfileDTO(
            final UserDto userDto, final UserPublicProfileDto userPublicProfileDto) {
        assertThat(userPublicProfileDto.getUserId()).isEqualTo(userDto.getUserId());
        assertThat(userPublicProfileDto.getName()).isEqualTo(userDto.getName());
        assertThat(userPublicProfileDto.getCity()).isEqualTo(userDto.getCity());
        assertThat(userPublicProfileDto.getBooks()).isEqualTo(Set.of());
    }

    private static void checkUserDTOAndUserProfileDTO(
            final UserDto userDto, final UserProfileDto userProfileDto) {
        assertThat(userProfileDto.getUserId()).isEqualTo(userDto.getUserId());
        assertThat(userProfileDto.getName()).isEqualTo(userDto.getName());
        assertThat(userProfileDto.getCity()).isEqualTo(userDto.getCity());
        assertThat(userProfileDto.getLogin()).isEqualTo(userDto.getLogin());
        assertThat(userProfileDto.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(userProfileDto.getBooks()).isEqualTo(Set.of());
    }
}
