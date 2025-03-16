package io.github.enkarin.bookcrossing.admin.controllers;

import io.github.enkarin.bookcrossing.admin.dto.InfoUsersDto;
import io.github.enkarin.bookcrossing.admin.dto.LockedUserDto;
import io.github.enkarin.bookcrossing.admin.service.AdminService;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("PMD.UnusedPrivateMethod")
class AdminControllerTest extends BookCrossingBaseTests {

    @Autowired
    private AdminService adminService;

    @Test
    void userListShouldWorkWithEmptyUserList() {
        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("adm")
                .queryParam("zone", 0)
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
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
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
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
        assertThat(webClient.post()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("adm", "locked")
                .queryParam("zone", 0)
                .build())
            .headers(this::setBearerAuth)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(LockedUserDto.builder().build())
            .exchange()
            .expectStatus().isEqualTo(406)
            .expectBody(new ParameterizedTypeReference<Map<String, List<String>>>() {
            }).returnResult().getResponseBody())
            .isNotEmpty()
            .satisfies(m -> assertThat(m.get("errorList")).containsExactlyInAnyOrder("3004", "3003"));
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
