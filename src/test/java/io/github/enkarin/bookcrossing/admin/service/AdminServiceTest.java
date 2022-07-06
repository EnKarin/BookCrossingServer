package io.github.enkarin.bookcrossing.admin.service;

import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.registation.dto.UserDto;
import io.github.enkarin.bookcrossing.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdminServiceTest extends BookCrossingBaseTests {

    private final List<Integer> usersId = new ArrayList<>();

    @Autowired
    private AdminService adminService;

    @AfterEach
    void delete() {
        userService.deleteUser(usersId.get(0));
    }

    @Test
    void alockedUser() {
        User user = userService.saveUser(UserDto.create("Tester", "Test", "123456",
                "123456", "k.test@mail.ru", "NSK"));
        usersId.add(user.getUserId());
    }

    @Test
    void lockedUserException() {
    }

    @Test
    void nonLockedUser() {
    }

    @Test
    void nonLockedUserException() {
    }

    @Test
    void findAllUsers() {
        assertThat(adminService.findAllUsers(0)).hasSize(0);
    }

    @Test
    void findAllUsersEmpty() {
    }
}