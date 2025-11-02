package io.github.enkarin.bookcrossing.chat.controllers;

import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.chat.dto.MessageDto;
import io.github.enkarin.bookcrossing.chat.dto.MessagePutRequest;
import io.github.enkarin.bookcrossing.chat.dto.MessageRequest;
import io.github.enkarin.bookcrossing.chat.dto.UsersCorrKeyDto;
import io.github.enkarin.bookcrossing.chat.service.CorrespondenceService;
import io.github.enkarin.bookcrossing.chat.service.MessageService;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

class MessageControllerTest extends BookCrossingBaseTests {

    @Autowired
    private CorrespondenceService correspondenceService;
    @Autowired
    private MessageService messageService;

    @Test
    void sendMessageShouldWork() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());

        final var key = correspondenceService.createChat(userBot.getUserId(), userAlex.getLogin());

        final var response = execute(HttpMethod.POST, TestDataProvider.buildMessageRequest(key), 200)
                .expectBody(MessageDto.class)
                .returnResult().getResponseBody();

        assertThat(response)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("messageId", "departureDate") //Temporary date ignore
                .isEqualTo(TestDataProvider.buildMessageDto(userBot.getUserId(), Long.MAX_VALUE, ""));
    }

    @Test
    void sendMessageShouldFailWithChatNotFound() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);
        final var userAlexId = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();
        enabledUser(userAlexId);

        execute(HttpMethod.POST, TestDataProvider.buildMessageRequest(UsersCorrKeyDto.builder()
                .firstUserId(userBotId)
                .secondUserId(userAlexId)
                .build()), 404)
                .expectBody()
                .jsonPath("$.correspondence").isEqualTo("Чата не существует");
    }

    @Test
    void sendMessageShouldFailWithBindingError() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());

        final var key = correspondenceService.createChat(userAlex.getUserId(), userAlex.getLogin());

        execute(HttpMethod.POST, MessageRequest.builder().usersCorrKeyDto(key).build(), 406)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Сообщение не может быть пустым");
    }

    @Test
    void sendMessageShouldFailWithNoAccessToChat() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());

        final var key = correspondenceService.createChat(userAlex.getUserId(), userAlex.getLogin());

        execute(HttpMethod.POST, TestDataProvider.buildMessageRequest(key), 403)
                .expectBody()
                .jsonPath("$.correspondence").isEqualTo("Нет доступа к чату");
    }

    @Test
    void putMessageShouldWork() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());

        final var key = correspondenceService.createChat(userBot.getUserId(), userAlex.getLogin());
        final var messageId = messageService.sendMessage(TestDataProvider.buildMessageRequest(key), userBot.getLogin()).getMessageId();

        final var response = execute(HttpMethod.PUT, TestDataProvider.buildMessagePutRequest(messageId), 200)
                .expectBody(MessageDto.class)
                .returnResult().getResponseBody();

        assertThat(response)
                .isNotNull()
                .extracting(MessageDto::getText)
                .isEqualTo("New");
    }

    @Test
    void putMessageShouldFailWithBindingError() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());

        final var key = correspondenceService.createChat(userBot.getUserId(), userAlex.getLogin());
        final var messageId = messageService.sendMessage(TestDataProvider.buildMessageRequest(key), userBot.getLogin()).getMessageId();

        execute(HttpMethod.PUT, MessagePutRequest.builder().messageId(messageId).build(), 406)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Сообщение не может быть пустым");
    }

    @Test
    void putMessageShouldFailWithMessageNotFound() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        execute(HttpMethod.PUT, TestDataProvider.buildMessagePutRequest(Long.MAX_VALUE), 404)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Сообщения не существует");
    }

    @Test
    void putMessageShouldFailWithUserIsNotSender() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());

        final var key = correspondenceService.createChat(userBot.getUserId(), userAlex.getLogin());
        final var messageId = messageService.sendMessage(TestDataProvider.buildMessageRequest(key), userAlex.getLogin()).getMessageId();

        execute(HttpMethod.PUT, TestDataProvider.buildMessagePutRequest(messageId), 403)
                .expectBody()
                .jsonPath("$.correspondence").isEqualTo("Пользователь не является отправителем");
    }

    @Test
    void deleteForEveryoneMessageShouldWork() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());

        final var key = correspondenceService.createChat(userBot.getUserId(), userAlex.getLogin());
        final var messageId = messageService.sendMessage(TestDataProvider.buildMessageRequest(key), userBot.getLogin()).getMessageId();

        execute(messageId, 200);

        assertThat(correspondenceService.getChat(key.getFirstUserId(), key.getSecondUserId(), 0, userBot.getLogin()))
                .isEmpty();
        assertThat(correspondenceService.getChat(key.getFirstUserId(), key.getSecondUserId(), 0, userAlex.getLogin()))
                .isEmpty();
    }

    @Test
    void deleteForEveryoneMessageShouldFailWithUserIsNotSender() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());

        final var key = correspondenceService.createChat(userBot.getUserId(), userAlex.getLogin());
        final var messageId = messageService.sendMessage(TestDataProvider.buildMessageRequest(key), userAlex.getLogin()).getMessageId();

        execute(messageId, 403)
                .expectBody()
                .jsonPath("$.correspondence").isEqualTo("Пользователь не является отправителем");
    }

    @Test
    void deleteForEveryoneMessageShouldFailWithMessageNotFound() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        execute(Long.MAX_VALUE, 404)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Сообщения не существует");
    }

    @Test
    void deleteForMeMessageShouldWorkForFirstUser() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());

        final var key = correspondenceService.createChat(userBot.getUserId(), userAlex.getLogin());
        final var messageId = messageService.sendMessage(TestDataProvider.buildMessageRequest(key), userBot.getLogin()).getMessageId();

        executeDeleteForMe(messageId, generateAccessToken(TestDataProvider.buildAuthBot()), 200);

        assertThat(correspondenceService.getChat(key.getFirstUserId(), key.getSecondUserId(), 0, userBot.getLogin()))
                .isEmpty();
        assertThat(correspondenceService.getChat(key.getFirstUserId(), key.getSecondUserId(), 0, userAlex.getLogin()))
                .hasSize(1);
    }

    @Test
    void deleteForMeMessageShouldWorkForSecondUser() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());

        final var key = correspondenceService.createChat(userBot.getUserId(), userAlex.getLogin());
        final var messageId = messageService.sendMessage(TestDataProvider.buildMessageRequest(key), userBot.getLogin()).getMessageId();

        executeDeleteForMe(messageId, generateAccessToken(TestDataProvider.buildAuthAlex()), 200);

        assertThat(correspondenceService.getChat(key.getFirstUserId(), key.getSecondUserId(), 0, userBot.getLogin()))
                .hasSize(1);
        assertThat(correspondenceService.getChat(key.getFirstUserId(), key.getSecondUserId(), 0, userAlex.getLogin()))
                .isEmpty();
    }

    @Test
    void deleteForMeMessageShouldFailWithMessageNotFound() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        executeDeleteForMe(Long.MAX_VALUE, generateAccessToken(TestDataProvider.buildAuthBot()), 404)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Сообщения не существует");
    }

    @Test
    void deleteForMeMessageShouldFailWithNoAccessToChat() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());

        final var key = correspondenceService.createChat(userAlex.getUserId(), userAlex.getLogin());
        final var messageId = messageService.sendMessage(TestDataProvider.buildMessageRequest(key), userAlex.getLogin()).getMessageId();

        executeDeleteForMe(messageId, generateAccessToken(TestDataProvider.buildAuthBot()), 403)
                .expectBody()
                .jsonPath("$.correspondence").isEqualTo("Нет доступа к чату");
    }

    private WebTestClient.ResponseSpec execute(final HttpMethod method, final Object body, final int status) {
        return webTestClient.method(method)
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("user", "correspondence", "message")
                        .build())
                .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
                .bodyValue(body)
                .exchange()
                .expectStatus().isEqualTo(status);
    }

    private WebTestClient.ResponseSpec execute(final long messageId, final int status) {
        return webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("user", "correspondence", "message")
                        .queryParam("messageId", messageId)
                        .build())
                .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
                .exchange()
                .expectStatus().isEqualTo(status);
    }

    private WebTestClient.ResponseSpec executeDeleteForMe(final long messageId, final String token, final int status) {
        return webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("user", "correspondence", "message", "deleteForMe")
                        .queryParam("messageId", messageId)
                        .build())
                .headers(headers -> headers.setBearerAuth(token))
                .exchange()
                .expectStatus().isEqualTo(status);
    }
}
