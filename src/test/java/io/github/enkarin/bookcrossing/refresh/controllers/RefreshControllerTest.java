package io.github.enkarin.bookcrossing.refresh.controllers;

import io.github.enkarin.bookcrossing.refresh.dto.RefreshRequest;
import io.github.enkarin.bookcrossing.refresh.service.RefreshService;
import io.github.enkarin.bookcrossing.registration.dto.AuthResponse;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshControllerTest extends BookCrossingBaseTests {

    @Autowired
    private RefreshService refreshService;

    @Test
    void updateTokensShouldWork() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final AuthResponse tokens = refreshService.createTokens(user.getLogin());

        final AuthResponse body = webTestClient.post()
                .uri("/refresh")
                .bodyValue(RefreshRequest.create(tokens.getRefreshToken()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(body)
                .isNotEqualTo(tokens);

        assertThat(jdbcTemplate.queryForObject("select exists(select * from bookcrossing.t_refresh where refresh_id = ?)",
                Boolean.class, tokens.getRefreshToken())).isFalse();
        assertThat(jdbcTemplate.queryForObject("select exists(select * from bookcrossing.t_refresh where refresh_id = ?)",
                Boolean.class, body.getRefreshToken())).isTrue();
    }
}
