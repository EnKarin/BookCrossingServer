package io.github.enkarin.bookcrossing.registation.controllers;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.errors.ErrorListResponse;
import io.github.enkarin.bookcrossing.registation.dto.AuthResponse;
import io.github.enkarin.bookcrossing.registation.dto.UserRegistrationDto;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static io.github.enkarin.bookcrossing.init.GreenMailInitializer.GREEN_MAIL_CONTAINER;
import static org.assertj.core.api.Assertions.assertThat;

class RegistrationControllerTest extends BookCrossingBaseTests {

    private static GreenMail greenMail;

    @BeforeAll
    static void setUp() {
        greenMail = new GreenMail(new ServerSetup(GREEN_MAIL_CONTAINER.getFirstMappedPort(),
                GREEN_MAIL_CONTAINER.getHost(), "smtp"))
                .withConfiguration(GreenMailConfiguration.aConfig().withDisabledAuthentication());
        greenMail.start();
    }

    @Test
    void registerUserTest() throws MessagingException {
        final UserRegistrationDto registrationDto = TestDataProvider.buildMax();
        final UserDto user = checkPost("/registration", registrationDto, 201)
                .expectBody(UserDto.class)
                .returnResult().getResponseBody();
        assertThat(user).isNotNull();

        usersId.add(user.getUserId());

        assertThat(greenMail.getReceivedMessagesForDomain(user.getEmail()))
                .extracting(MimeMessage::getAllRecipients)
                .hasSize(1)
                .contains(new Address[]{new InternetAddress(registrationDto.getEmail())});
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

    @Test
    void registerEmailExceptionTest() {
        usersId.add(userService.saveUser(TestDataProvider.prepareUser()
                .login("Bot2")
                .email("k.test@mail.ru")
                .build()).getUserId());
        checkPost("/registration", TestDataProvider.buildBot(), 409)
                .expectBody()
                .jsonPath("$.email")
                .isEqualTo("Пользователь с таким почтовым адресом уже существует");
    }

    //TODO: set up the time and compare tokens
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

    //TODO: set up the time and compare tokens
    @Test
    void mailConfirmTest() {
        final UserDto user = userService.saveUser(TestDataProvider.buildAlex());
        usersId.add(user.getUserId());
        final MimeMessage message = greenMail.getReceivedMessagesForDomain(user.getEmail())[0];
        final String token = new String(Base64.getMimeDecoder().decode(GreenMailUtil.getBody(message)),
                StandardCharsets.UTF_8)
                .split("token=")[1];
        checkToken(token, 200);
        assertThat(jdbcTemplate.queryForObject("select enabled from t_user where user_id = ?", Boolean.class,
                        user.getUserId()))
                .isTrue();
    }

    @Test
    void mailConfirmTokenExceptionTest() {
        checkToken("token", 404);
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
