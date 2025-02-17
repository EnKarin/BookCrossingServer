package io.github.enkarin.bookcrossing.user.service;

import io.github.enkarin.bookcrossing.exception.EmailFailedException;
import io.github.enkarin.bookcrossing.exception.InvalidPasswordException;
import io.github.enkarin.bookcrossing.exception.LoginFailedException;
import io.github.enkarin.bookcrossing.exception.PasswordsDontMatchException;
import io.github.enkarin.bookcrossing.exception.TokenNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.registration.dto.LoginRequest;
import io.github.enkarin.bookcrossing.registration.dto.UserRegistrationDto;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import io.github.enkarin.bookcrossing.user.dto.UserProfileDto;
import io.github.enkarin.bookcrossing.user.dto.UserPublicProfileDto;
import io.github.enkarin.bookcrossing.user.dto.UserPutProfileDto;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class UserServiceTest extends BookCrossingBaseTests {
    private static final int GM_TIME_ZERO = 0;

    @Test
    void saveUserCorrectUserTest() {
        assertThat(userService.findById(createAndSaveUser(TestDataProvider.buildAlex()).getUserId(), GM_TIME_ZERO)).isNotNull();
    }

    @Test
    void saveUserNonConfirmedPasswordTest() {
        final var nonConfirmedUser = TestDataProvider.buildNonConfirmedPasswordUser();
        assertThatThrownBy(() -> userService.saveUser(nonConfirmedUser))
            .isInstanceOf(PasswordsDontMatchException.class)
            .hasMessage("Пароли не совпадают");
    }

    @Test
    void saveUserDuplicatedUserTest() {
        createAndSaveUser(TestDataProvider.buildAlex());
        final var user = TestDataProvider.buildAlex();
        assertThatThrownBy(() -> userService.saveUser(user))
            .isInstanceOf(LoginFailedException.class)
            .hasMessage("Пользователь с таким логином уже существует");
    }

    @Test
    void saveUserDuplicatedEmailTest() {
        createAndSaveUser(TestDataProvider.buildAlex());
        final var userWithRegisteredEmail = TestDataProvider.buildUserWithAlexEmail();
        assertThatThrownBy(() -> userService.saveUser(userWithRegisteredEmail))
            .isInstanceOf(EmailFailedException.class)
            .hasMessage("Пользователь с таким почтовым адресом уже существует");
    }

    @Test
    void loginOnEmail() {
        final UserRegistrationDto userDto = TestDataProvider.buildAlex();
        enabledUser(createAndSaveUser(userDto).getUserId());

        assertThat(userService.findByLoginAndPassword(LoginRequest.create(userDto.getEmail(), userDto.getPassword(), 7))).isNotNull();
    }

    @Test
    void confirmMailWrongTokenTest() {
        assertThatThrownBy(() -> userService.confirmMail("wrong token"))
            .isInstanceOf(TokenNotFoundException.class)
            .hasMessage("Токен не найден");
    }

    @Test
    void findByLoginCorrectTest() {
        assertThat(userService.findByLogin(createAndSaveUser(TestDataProvider.buildAlex()).getLogin())).isNotNull();
    }

    @Test
    void findByLoginNonExistingUserTest() {
        assertThatThrownBy(() -> userService.findByLogin("Non existing login"))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("Пользователь не найден");
    }

    @Test
    void getProfileTest() {
        final UserDto userDTO = createAndSaveUser(TestDataProvider.buildAlex());
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
            .map(this::createAndSaveUser)
            .toList();

        final var foundUsers = userService.findAllUsers(GM_TIME_ZERO);
        assertThat(foundUsers)
            .hasSize(3)
            .hasSameSizeAs(users)
            .as("Rows should be sorted by user_id")
            .isSortedAccordingTo(Comparator.comparing(UserPublicProfileDto::getUserId));

        final var usersIterator = users.iterator();
        final var foundIterator = foundUsers.iterator();
        while (usersIterator.hasNext() && foundIterator.hasNext()) {
            checkUserDTOAndUserPublicPublicProfileDTO(usersIterator.next(), foundIterator.next());
        }
    }

    @Test
    void deleteUserTest() {
        final var user = userService.saveUser(TestDataProvider.buildAlex()).getUserId();

        userService.deleteUser(user);
        assertThatThrownBy(() -> userService.findById(user, GM_TIME_ZERO))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("Пользователь не найден");
    }

    @Test
    void putUserInfoTest() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());

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
        final var user = createAndSaveUser(TestDataProvider.buildAlex()).getLogin();

        final UserPutProfileDto putProfile = UserPutProfileDto.create(
            "Mike",
            "123456",
            "654321",
            "123456",
            "Moscow"
        );

        assertThatThrownBy(() -> userService.putUserInfo(putProfile, user))
            .isInstanceOf(PasswordsDontMatchException.class)
            .hasMessage("Пароли не совпадают");
    }

    @Test
    void putUserInfoWrongOldPasswordTest() {
        final var user = createAndSaveUser(TestDataProvider.buildAlex()).getLogin();

        final UserPutProfileDto putProfile = UserPutProfileDto.create(
            "Mike",
            "wrong password",
            "654321",
            "654321",
            "Moscow"
        );

        assertThatThrownBy(() -> userService.putUserInfo(putProfile, user))
            .isInstanceOf(InvalidPasswordException.class)
            .hasMessage("Некорректный пароль");
    }

    @Test
    void registrationWithNullLogin() {
        final UserRegistrationDto userRegistrationDto = TestDataProvider.buildAlex();
        userRegistrationDto.setLogin(null);
        assertThat(createAndSaveUser(userRegistrationDto)).satisfies(userDto -> checkEqual(userDto, userRegistrationDto));
    }

    @Test
    void registrationWithEmptyLogin() {
        final UserRegistrationDto userRegistrationDto = TestDataProvider.buildMax();
        userRegistrationDto.setLogin("  ");
        assertThat(createAndSaveUser(userRegistrationDto)).satisfies(userDto -> checkEqual(userDto, userRegistrationDto));
    }

    private static void checkEqual(final UserDto userDto, final UserRegistrationDto userRegistrationDto) {
        assertThat(userDto.getName()).isEqualTo(userRegistrationDto.getName());
        assertThat(userDto.getCity()).isEqualTo(userRegistrationDto.getCity());
        assertThat(userDto.getEmail()).isEqualTo(userRegistrationDto.getEmail());
        assertThat(userDto.getLogin()).isNotBlank();
    }

    private static void checkUserDTOAndUserPublicPublicProfileDTO(final UserDto userDto, final UserPublicProfileDto userPublicProfileDto) {
        assertThat(Integer.parseInt(userPublicProfileDto.getUserId())).isEqualTo(userDto.getUserId());
        assertThat(userPublicProfileDto.getName()).isEqualTo(userDto.getName());
        assertThat(userPublicProfileDto.getCity()).isEqualTo(userDto.getCity());
    }

    private static void checkUserDTOAndUserProfileDTO(final UserDto userDto, final UserProfileDto userProfileDto) {
        assertThat(userProfileDto.getUserId()).isEqualTo(userDto.getUserId());
        assertThat(userProfileDto.getName()).isEqualTo(userDto.getName());
        assertThat(userProfileDto.getCity()).isEqualTo(userDto.getCity());
        assertThat(userProfileDto.getLogin()).isEqualTo(userDto.getLogin());
        assertThat(userProfileDto.getEmail()).isEqualTo(userDto.getEmail());
    }
}
