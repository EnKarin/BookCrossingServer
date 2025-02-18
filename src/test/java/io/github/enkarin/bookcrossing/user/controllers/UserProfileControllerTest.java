package io.github.enkarin.bookcrossing.user.controllers;

import io.github.enkarin.bookcrossing.constant.ErrorMessage;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserProfileDto;
import io.github.enkarin.bookcrossing.user.dto.UserPublicProfileDto;
import io.github.enkarin.bookcrossing.user.dto.UserPutProfileDto;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileControllerTest extends BookCrossingBaseTests {

    @Test
    void getMyProfileShouldWork() {
        final var user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        createAndSaveBooks(user.getLogin());

        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "profile")
                .queryParam("zone", 0)
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(UserProfileDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .isNotNull()
            .satisfies(r -> {
                assertThat(r)
                    .usingRecursiveComparison()
                    .ignoringFields("books")
                    .isEqualTo(TestDataProvider.buildProfileBot(user.getUserId()));
            });
    }

    @Test
    void getAlienProfileShouldWork() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();

        createAndSaveBooks(userBot.getLogin());

        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "profile")
                .queryParam("zone", 0)
                .build())
            .headers(headers -> {
                headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot()));
                headers.set("userId", String.valueOf(userAlex));
            })
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(UserPublicProfileDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .isNotNull()
            .isEqualTo(TestDataProvider.buildPublicProfileBot(userAlex));
    }

    @Test
    void putProfileShouldWork() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        final var response = webClient.put()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "profile")
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .bodyValue(TestDataProvider.preparePutProfile().build())
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(UserProfileDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .isNotNull()
            .isEqualTo(TestDataProvider.buildPutProfileBot(userBot.getUserId()));
    }

    @Test
    void putProfileShouldFailWithPasswordDontMatch() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        assertThatReturnException(TestDataProvider.preparePutProfile().passwordConfirm("123456").build(), 412, ErrorMessage.ERROR_1000.getCode());
    }

    @Test
    void putProfileShouldFailWithPasswordInvalid() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        assertThatReturnException(TestDataProvider.preparePutProfile().oldPassword("123457").build(), 409, ErrorMessage.ERROR_1007.getCode());
    }

    @Test
    void putProfileShouldFailWithBindingErrorsException() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        final var map = webClient.put()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "profile")
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .bodyValue(TestDataProvider.preparePutProfile().oldPassword("").build())
            .exchange()
            .expectStatus().isEqualTo(406).expectBody(new ParameterizedTypeReference<Map<String, List<String>>>() {
            }).returnResult().getResponseBody();
        assertThat(map)
            .isNotEmpty()
            .extracting(m -> m.get("errorList"))
            .isEqualTo(List.of("3012"));
    }

    @Test
    void getAllProfileShouldWork() {
        final var user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        createAndSaveBooks(user.getLogin());

        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "profile", "users")
                .queryParam("zone", 0)
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(UserPublicProfileDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .hasSize(1)
            .singleElement()
            .satisfies(r -> assertThat(r)
                .usingRecursiveComparison()
                .ignoringFields("books", "loginDate")
                .isEqualTo(TestDataProvider.buildPublicProfileBot(user.getUserId()))
            );
    }

    private void assertThatReturnException(final UserPutProfileDto putProfileDto, final int status, final String message) {
        webClient.put()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "profile")
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .bodyValue(putProfileDto)
            .exchange()
            .expectStatus().isEqualTo(status)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(message);
    }
}
