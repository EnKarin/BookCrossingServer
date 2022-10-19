package io.github.enkarin.bookcrossing.admin.controllers;

import io.github.enkarin.bookcrossing.admin.dto.InfoUsersDto;
import io.github.enkarin.bookcrossing.admin.dto.LockedUserDto;
import io.github.enkarin.bookcrossing.admin.service.AdminService;
import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

class AdminControllerTest extends BookCrossingBaseTests {

    @Autowired
    private AdminService adminService;

    @Test
    void userListShouldWorkWithEmptyUserList() {
        final var response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("adm")
                        .queryParam("zone", 0)
                        .build())
                .headers(this::setBearerAuth)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InfoUsersDto.class)
                .returnResult().getResponseBody();
        assertThat(response)
                .isEmpty();
    }

    @Test
    void userListShouldWork() {
        final var user = createAndSaveUser(TestDataProvider.buildBot());
        final var response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("adm")
                        .queryParam("zone", 0)
                        .build())
                .headers(this::setBearerAuth)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InfoUsersDto.class)
                .returnResult().getResponseBody();
        assertThat(response)
                .hasSize(1)
                .singleElement()
                .satisfies(r -> {
                    assertThat(r.getLogin())
                            .isEqualTo(user.getLogin());
                    assertThat(r.getEmail())
                            .isEqualTo(user.getEmail());
                });
    }

    @Test
    void lockedUserShouldFailWithBindingError() {
        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("adm", "locked")
                        .queryParam("zone", 0)
                        .build())
                .headers(this::setBearerAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(LockedUserDto.builder().build())
                .exchange()
                .expectStatus().isEqualTo(406)
                .expectBody()
                .jsonPath("$.login")
                .isEqualTo("Логин должен содержать хотя бы один видимый символ")
                .jsonPath("$.comment")
                .isEqualTo("Комментарий должен содержать хотя бы один видимый символ");
    }

    @Test
    void lockedUserShouldWork() {
        createAndSaveUser(TestDataProvider.buildBot());
        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("adm", "locked")
                        .queryParam("zone", 0)
                        .build())
                .headers(this::setBearerAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(LockedUserDto.create("Bot", "Заблокировано"))
                .exchange()
                .expectStatus().isEqualTo(200);
    }

    @Test
    void nonLockedUserShouldWork() {
        createAndSaveUser(TestDataProvider.buildBot());
        adminService.lockedUser(LockedUserDto.create("Bot", "Заблокировано"));
        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("adm", "nonLocked")
                        .queryParam("login", "Bot")
                        .build())
                .headers(this::setBearerAuth)
                .exchange()
                .expectStatus().isEqualTo(200);
    }

    private void setBearerAuth(@Nonnull final HttpHeaders headers) {
        headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthAdmin()));
    }
}
