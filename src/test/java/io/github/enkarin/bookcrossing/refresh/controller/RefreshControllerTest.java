package io.github.enkarin.bookcrossing.refresh.controller;

import com.icegreen.greenmail.util.GreenMailUtil;
import io.github.enkarin.bookcrossing.registration.dto.AuthResponse;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshControllerTest extends BookCrossingBaseTests {

    @Test
    void refreshShouldWork() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final MimeMessage message = GREEN_MAIL.getReceivedMessagesForDomain(user.getEmail())[0];
        final String token = new String(Base64.getMimeDecoder().decode(GreenMailUtil.getBody(message)),
            StandardCharsets.UTF_8)
            .split("token=")[1];
        final var responseConfirm = webClient.get()
            .uri("/registration/confirmation?token={token}", token)
            .exchange()
            .returnResult(AuthResponse.class);
        final String refresh = responseConfirm.getResponseCookies().get("refresh-token").get(0).getValue();

        final var response = webClient.post()
            .uri("/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .cookie("refresh-token", refresh)
            .exchange()
            .expectStatus().isOk()
            .expectBody(AuthResponse.class).returnResult();
        assertThat(response.getResponseCookies().get("refresh-token"))
            .isNotEmpty();
    }
}
