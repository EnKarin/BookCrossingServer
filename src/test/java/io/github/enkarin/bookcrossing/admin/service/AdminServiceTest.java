package io.github.enkarin.bookcrossing.admin.service;

import io.github.enkarin.bookcrossing.admin.dto.InfoUsersDto;
import io.github.enkarin.bookcrossing.admin.dto.LockedUserDto;
import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminServiceTest extends BookCrossingBaseTests {

    @Autowired
    private AdminService adminService;

    @Test
    void lockedUser() {
        final UserDto user = super.createAndSaveUser(TestDataProvider.buildAlex());
        assertThat(adminService.lockedUser(LockedUserDto.create(user.getLogin(), "Заблокировано"))).isFalse();
    }

    @Test
    void lockedUserException() {
        assertThatThrownBy(() -> adminService.lockedUser(LockedUserDto.create("Test", "Заблокировано")))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void nonLockedUser() {
        final UserDto user = super.createAndSaveUser(TestDataProvider.buildAlex());
        jdbcTemplate.update("update t_user set account_non_locked = 0 where user_id = " + user.getUserId());
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
                .map(super::createAndSaveUser)
                .collect(Collectors.toList());
        assertThat(adminService.findAllUsers(0))
                .hasSize(2)
                .hasSameElementsAs(users.stream()
                        .map(u -> InfoUsersDto.fromUserDto(u, 0))
                        .collect(Collectors.toList()));
    }

    @Test
    void findAllUsersEmpty() {
        assertThat(adminService.findAllUsers(0)).isEmpty();
    }
}
