package io.github.enkarin.bookcrossing.registation.controllers;

import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.errors.ErrorListResponse;
import io.github.enkarin.bookcrossing.registation.dto.AuthResponse;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationControllerTest extends BookCrossingBaseTests {

    //TODO: Проверка письма и тесты подтверждения почты
    @Test
    void registerUserTest() {
        final UserDto user = checkPost("/registration", TestDataProvider.buildBot(), 201)
                .expectBody(UserDto.class)
                .returnResult().getResponseBody();
        assertThat(user).isNotNull();

        usersId.add(user.getUserId());
    }

    @Test
    void registerBadEmailExceptionTest() {
        final var response = checkPost("/registration",
                TestDataProvider.prepareUser().login("User").email("t.test.mail.ru").build(),
                400)
                .expectBody(ErrorListResponse.class)
                .returnResult().getResponseBody();
        assertThat(response)
                .extracting(ErrorListResponse::getErrors)
                .isEqualTo(List.of("email: Некорректный почтовый адрес"));
    }

    @Test
    void registerPasswordConflictExceptionTest() {
        checkPost("/registration",
                TestDataProvider.prepareUser().login("User").email("t.test@mail.ru").password("7654321").build(),
                409)
                .expectBody()
                .jsonPath("$.password")
                .isEqualTo("Пароли не совпадают");
    }

    @Test
    void registerLoginExceptionTest() {
        usersId.add(userService.saveUser(TestDataProvider.buildBot()).getUserId());
        checkPost("/registration", TestDataProvider.buildBot(), 409)
                .expectBody()
                .jsonPath("$.login")
                .isEqualTo("Пользователь с таким логином уже существует");
    }

    //TODO: добавить сравнение токенов
    @Test
    void authTest() {
        final int userId = userService.saveUser(TestDataProvider.buildBot()).getUserId();
        usersId.add(userId);
        jdbcTemplate.update("update t_user set enabled = 1 where user_id = " + userId);
        final var response = checkPost("/auth", TestDataProvider.buildAuthBot(), 200)
                .expectBody(AuthResponse.class)
                .returnResult().getResponseBody();
        assertThat(response).isNotNull();
    }

    @Test
    void authNonConfirmExceptionTest() {
        usersId.add(userService.saveUser(TestDataProvider.buildBot()).getUserId());
        checkPost("/auth", TestDataProvider.buildAuthBot(), 403)
                .expectBody()
                .jsonPath("$.user")
                .isEqualTo("Аккаунт не подтвержден");
    }

    @Test
    void authLockedExceptionTest() {
        final int user = userService.saveUser(TestDataProvider.buildBot()).getUserId();
        usersId.add(user);
        jdbcTemplate.update("update t_user set enabled = 1, account_non_locked = 0 where user_id = " + user);
        checkPost("/auth", TestDataProvider.buildAuthBot(), 403)
                .expectBody()
                .jsonPath("$.user")
                .isEqualTo("Аккаунт заблокирован");
    }

    @Test
    void authInvalidPasswordExceptionTest() {
        final int user = userService.saveUser(TestDataProvider.buildBot()).getUserId();
        usersId.add(user);
        checkPost("/auth",
                TestDataProvider.prepareLogin().login("Bot").password("654321").build(),
                404)
                .expectBody()
                .jsonPath("$.user")
                .isEqualTo("Пользователь не найден");
    }

    private WebTestClient.ResponseSpec checkPost(final String uri, final Object body, final int status) {
        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isEqualTo(status);
    }
}
