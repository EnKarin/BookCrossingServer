package io.github.enkarin.bookcrossing.user.controllers;

import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserPasswordDto;
import org.junit.jupiter.api.Test;

class PasswordResetControllerTest extends BookCrossingBaseTests {

    @Test
    void sendMessageShouldFailWithUserNotFound() {
        webClient.post()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("reset", "send")
                .queryParam("email", "k@mail.ru")
                .build())
            .exchange()
            .expectStatus().isEqualTo(404)
            .expectBody()
            .jsonPath("$.user")
            .isEqualTo("Пользователь не найден");
    }

    @Test
    void updatePasswordShouldWork() {
        createAndSaveUser(TestDataProvider.buildBot());
        webClient.post()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("reset", "send")
                .queryParam("email", "k.test@mail.ru")
                .build())
            .exchange()
            .expectStatus().isEqualTo(200);

        final var token = jdbcTemplate.queryForObject("select confirmation_mail from bookcrossing.t_action_mail_user where type = 1", String.class);
        webClient.post()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("reset", "update")
                .queryParam("token", token)
                .build())
            .bodyValue(TestDataProvider.buildUserPasswordDto())
            .exchange()
            .expectStatus().isEqualTo(201);
    }

    @Test
    void updatePasswordShouldFailWithBindingException() {
        assertThatReturnException(TestDataProvider.buildInvalidUserPasswordDto(), 409, "$.password", "Пароли не совпадают");
    }

    @Test
    void updatePasswordShouldFailWithTokenInvalid() {
        createAndSaveUser(TestDataProvider.buildBot());

        assertThatReturnException(TestDataProvider.buildUserPasswordDto(), 403, "$.token", "Токен недействителен");
    }

    private void assertThatReturnException(final UserPasswordDto userPasswordDto, final int status, final String path, final String message) {
        webClient.post()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("reset", "update")
                .queryParam("token", "easd")
                .build())
            .bodyValue(userPasswordDto)
            .exchange()
            .expectStatus().isEqualTo(status)
            .expectBody()
            .jsonPath(path).isEqualTo(message);
    }
}
