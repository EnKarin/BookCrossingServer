package io.github.enkarin.bookcrossing.service;

import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.exception.EmailFailedException;
import io.github.enkarin.bookcrossing.exception.LoginFailedException;
import io.github.enkarin.bookcrossing.exception.PasswordsDontMatchException;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserServiceTest extends BookCrossingBaseTests {
    @AfterEach
    void delete() {
        usersId.forEach(u -> userService.deleteUser(u));
        usersId.clear();
    }

    @Test
    void saveUser_CorrectUser_Test() {
        final User user = userService.saveUser(TestDataProvider.buildAlex());
        usersId.add(user.getUserId());

        assertThat(userService.findById(user.getUserId())).isNotNull();
    }

    @Test
    void saveUser_NonConfirmedPassword_Test() {
        assertThatThrownBy(() -> userService.saveUser(TestDataProvider.buildNonConfirmedPasswordUser()))
                .isInstanceOf(PasswordsDontMatchException.class)
                .hasMessage("Пароли не совпадают");
    }

    @Test
    void saveUser_DuplicatedUser_Test() {
        final User user = userService.saveUser(TestDataProvider.buildAlex());
        usersId.add(user.getUserId());

        assertThatThrownBy(() -> userService.saveUser(TestDataProvider.buildAlex()))
                .isInstanceOf(LoginFailedException.class)
                .hasMessage("login: Пользователь с таким логином уже существует");
    }

    @Test
    void saveUser_DuplicatedEmail_Test() {
        final User user = userService.saveUser(TestDataProvider.buildAlex());
        usersId.add(user.getUserId());

        assertThatThrownBy(() -> userService.saveUser(TestDataProvider.buildUserWithAlexEmail()))
                .isInstanceOf(EmailFailedException.class)
                .hasMessage("email: Пользователь с таким почтовым адресом уже существует");
    }
}
