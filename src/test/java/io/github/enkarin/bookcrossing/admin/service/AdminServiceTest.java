package io.github.enkarin.bookcrossing.admin.service;

import io.github.enkarin.bookcrossing.admin.dto.InfoUsersDto;
import io.github.enkarin.bookcrossing.admin.dto.LockedUserDto;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminServiceTest extends BookCrossingBaseTests {

    @Autowired
    private AdminService adminService;

    @Test
    void lockedUser() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        assertThat(adminService.lockedUser(LockedUserDto.create(user.getLogin(), "Заблокировано"))).isFalse();
    }

    @Test
    void lockedUserException() {
        final LockedUserDto dto = LockedUserDto.create("Test", "Заблокировано");
        assertThatThrownBy(() -> adminService.lockedUser(dto))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("Пользователь не найден");
    }

    @Test
    void nonLockedUser() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        jdbcTemplate.update("update bookcrossing.t_user set account_non_locked = false where user_id = " + user.getUserId());
        assertThat(adminService.nonLockedUser(user.getLogin())).isTrue();
    }

    @Test
    void nonLockedUserException() {
        assertThatThrownBy(() -> adminService.nonLockedUser("Test"))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("Пользователь не найден");
    }

    @Test
    void findAllUsers() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();
        assertThat(adminService.findAllUsers(0, 0, 5))
            .hasSize(3)
            .hasSameElementsAs(users.stream()
                .map(u -> InfoUsersDto.fromUserDto(u, 0))
                .toList());
    }

    @Test
    void findAllUsersWithPagination() {
        final Set<InfoUsersDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .map(u -> InfoUsersDto.fromUserDto(u, 0))
            .collect(Collectors.toSet());
        assertThat(adminService.findAllUsers(0, 0, 2))
            .hasSize(2)
            .allMatch(users::contains);
    }

    @Test
    void findAllUsersEmpty() {
        assertThat(adminService.findAllUsers(0, 0, 2)).isEmpty();
    }
}
