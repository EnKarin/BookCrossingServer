package io.github.enkarin.bookcrossing.admin.service;

import io.github.enkarin.bookcrossing.admin.dto.InfoUsersDto;
import io.github.enkarin.bookcrossing.admin.dto.LockedUserDto;
import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.registation.dto.UserDto;
import io.github.enkarin.bookcrossing.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminServiceTest extends BookCrossingBaseTests {

    @Autowired
    private AdminService adminService;

    @AfterEach
    void delete() {
        usersId.forEach(u -> userService.deleteUser(u));
        usersId.clear();
    }

    @Test
    void lockedUser() {
        final User user = userService.saveUser(UserDto.create("Tester", "Test", "123456",
                "123456", "k.test@mail.ru", "NSK"));
        usersId.add(user.getUserId());
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
        final User user = userService.saveUser(UserDto.create("Tester", "Test", "123456",
                "123456", "k.test@mail.ru", "NSK"));
        jdbcTemplate.update("update t_user set account_non_locked = 0 where user_id = " + user.getUserId());
        usersId.add(user.getUserId());
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
        final User user1 = userService.saveUser(UserDto.create("Tester", "Test", "123456",
                "123456", "k.test@mail.ru", "NSK"));
        final User user2 = userService.saveUser(UserDto.create("Test", "Test2", "123456",
                "123456", "t.test@mail.ru", "NSK"));
        usersId.add(user1.getUserId());
        usersId.add(user2.getUserId());
        assertThat(adminService.findAllUsers(0))
                .hasSize(2)
                .containsExactlyInAnyOrder(InfoUsersDto.fromUser(user1, 0),
                        InfoUsersDto.fromUser(user2, 0));
    }

    @Test
    void findAllUsersEmpty() {
        assertThat(adminService.findAllUsers(0)).hasSize(0);
    }
}
