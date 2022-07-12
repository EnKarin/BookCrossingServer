package io.github.enkarin.bookcrossing.registation.controllers;

import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.init.GreenMailInitializer;
import io.github.enkarin.bookcrossing.registation.dto.AuthResponse;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import javax.mail.MessagingException;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(initializers = GreenMailInitializer.class)
class RegistrationControllerTest extends BookCrossingBaseTests {

    @AfterEach
    void delete() {
        usersId.forEach(u -> userService.deleteUser(u));
        usersId.clear();
    }

    @Test
    void registerUserTest() throws MessagingException {
        UserDto user = webClient.post()
                .uri("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(TestDataProvider.buildBot())
                .exchange()
                .expectStatus().isEqualTo(201)
                .expectBody(UserDto.class)
                .returnResult().getResponseBody();
        assertThat(user).isNotNull();

        usersId.add(user.getUserId());

//        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
//        assertThat(receivedMessage.getAllRecipients())
//                .hasSize(1)
//                .extracting(Address::toString)
//                .isEqualTo(List.of(user.getEmail()));
    }

    @Test
    void authTest() {
        int userId = userService.saveUser(TestDataProvider.buildBot()).getUserId();
        usersId.add(userId);
        jdbcTemplate.update("update t_user set enabled = 1 where user_id = " + userId);
        var response = webClient.post()
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