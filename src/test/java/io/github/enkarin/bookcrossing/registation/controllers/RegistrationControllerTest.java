package io.github.enkarin.bookcrossing.registation.controllers;

import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.registation.dto.AuthResponse;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationControllerTest extends BookCrossingBaseTests {

    @Test
    void registerUserTest() {
        final UserDto user = webClient.post()
                .uri("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(TestDataProvider.buildBot())
                .exchange()
                .expectStatus().isEqualTo(201)
                .expectBody(UserDto.class)
                .returnResult().getResponseBody();
        assertThat(user).isNotNull();

        usersId.add(user.getUserId());
    }

    @Test
    void authTest() {
        final int userId = userService.saveUser(TestDataProvider.buildBot()).getUserId();
        usersId.add(userId);
        jdbcTemplate.update("update t_user set enabled = 1 where user_id = " + userId);
        final var response = webClient.post()
                .uri("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(TestDataProvider.buildAuthBot())
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectBody(AuthResponse.class)
                .returnResult().getResponseBody();
        assertThat(response).isNotNull();
    }
}
