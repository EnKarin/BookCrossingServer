package io.github.enkarin.bookcrossing.registration.controllers;

import com.icegreen.greenmail.util.GreenMailUtil;
import io.github.enkarin.bookcrossing.registration.dto.AuthResponse;
import io.github.enkarin.bookcrossing.registration.dto.UserRegistrationDto;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationControllerTest extends BookCrossingBaseTests {

    @Test
    void registerUserTest() throws MessagingException {
        final UserRegistrationDto registrationDto = TestDataProvider.buildMax();
        final UserDto user = checkPost("/registration", registrationDto, 201)
                .expectBody(UserDto.class)
                .returnResult().getResponseBody();
        assertThat(user).isNotNull();

        trackUserId(user.getUserId());

        assertThat(GREEN_MAIL.getReceivedMessagesForDomain(user.getEmail()))
                .extracting(MimeMessage::getAllRecipients)
                .hasSize(1)
                .contains(new Address[]{new InternetAddress(registrationDto.getEmail())});
        assertThat(GREEN_MAIL.getReceivedMessagesForDomain(user.getEmail()))
                .hasSize(1)
                .satisfies(mimeMessages -> {
                    assertThat(mimeMessages)
                            .extracting(MimeMessage::getAllRecipients)
                            .hasSize(1)
                            .contains(new Address[]{new InternetAddress(registrationDto.getEmail())});
                    assertThat(mimeMessages)
                            .extracting(MimeMessage::getFrom)
                            .hasSize(1)
                            .contains(new Address[]{new InternetAddress("ShareBook.inc@gmail.com")});
                });
    }

    @Test
    void registerBadEmailExceptionTest() {
        checkPost("/registration", TestDataProvider.prepareUser().login("User").email("t.test.mail.ru").build(),
                406)
                .expectBody()
                .jsonPath("$.email")
                .isEqualTo("Некорректный почтовый адрес");
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
        createAndSaveUser(TestDataProvider.buildBot());
        checkPost("/registration", TestDataProvider.buildBot(), 409)
                .expectBody()
                .jsonPath("$.login")
                .isEqualTo("Пользователь с таким логином уже существует");
    }

    @Test
    void registerEmailExceptionTest() {
        createAndSaveUser(TestDataProvider.prepareUser()
                .login("Bot2")
                .email("k.test@mail.ru")
                .build());
        checkPost("/registration", TestDataProvider.buildBot(), 409)
                .expectBody()
                .jsonPath("$.email")
                .isEqualTo("Пользователь с таким почтовым адресом уже существует");
    }

    //TODO: set up the time and compare tokens
    @Test
    void authTest() {
        final int userId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        jdbcTemplate.update("update bookcrossing.t_user set enabled = true where user_id = " + userId);
        final var response = checkPost("/auth", TestDataProvider.buildAuthBot(), 200)
                .expectBody(AuthResponse.class)
                .returnResult();
        assertThat(response)
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.getResponseBody())
                            .isNotNull();
                    assertThat(r.getResponseCookies().get("refresh-token"))
                            .isNotEmpty();
                });
    }

    @Test
    void authNonConfirmExceptionTest() {
        createAndSaveUser(TestDataProvider.buildBot());
        checkPost("/auth", TestDataProvider.buildAuthBot(), 403)
                .expectBody()
                .jsonPath("$.user")
                .isEqualTo("Аккаунт не подтвержден");
    }

    @Test
    void authLockedExceptionTest() {
        final int userId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        jdbcTemplate.update("update bookcrossing.t_user set enabled = true, account_non_locked = false where user_id = " + userId);
        checkPost("/auth", TestDataProvider.buildAuthBot(), 403)
                .expectBody()
                .jsonPath("$.user")
                .isEqualTo("Аккаунт заблокирован");
    }

    @Test
    void authInvalidPasswordExceptionTest() {
        createAndSaveUser(TestDataProvider.buildBot());
        checkPost("/auth",
                TestDataProvider.prepareLogin().login("Bot").password("654321").build(),
                404)
                .expectBody()
                .jsonPath("$.user")
                .isEqualTo("Пользователь не найден");
    }

    //TODO: set up the time and compare tokens
    @Test
    void mailConfirmTest() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final MimeMessage message = GREEN_MAIL.getReceivedMessagesForDomain(user.getEmail())[0];
        final String token = new String(Base64.getMimeDecoder().decode(GreenMailUtil.getBody(message)),
                StandardCharsets.UTF_8)
                .split("token=")[1];
        final var response = checkToken(token, 200);
        assertThat(response.returnResult(AuthResponse.class))
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.getResponseBody())
                            .isNotNull();
                    assertThat(r.getResponseCookies().get("refresh-token"))
                            .isNotEmpty();
                });
        assertThat(jdbcTemplate.queryForObject("select enabled from bookcrossing.t_user where user_id = ?", Boolean.class,
                user.getUserId()))
                .isTrue();
    }

    @Test
    void mailConfirmTokenExceptionTest() {
        checkToken("token", 404);
    }

    @Test
    void generateToken() {
        webClient.get()
            .uri("/generate-login")
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.originalLogin").isNotEmpty();
    }

    private WebTestClient.ResponseSpec checkPost(final String uri, final Object body, final int status) {
        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isEqualTo(status);
    }

    private WebTestClient.ResponseSpec checkToken(final String token, final int status) {
        return webClient.get()
                .uri("/registration/confirmation?token={token}", token)
                .exchange()
                .expectStatus().isEqualTo(status);
    }
}
