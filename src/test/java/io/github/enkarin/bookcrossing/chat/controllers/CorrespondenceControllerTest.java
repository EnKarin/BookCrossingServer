package io.github.enkarin.bookcrossing.chat.controllers;

import io.github.enkarin.bookcrossing.admin.dto.LockedUserDto;
import io.github.enkarin.bookcrossing.admin.service.AdminService;
import io.github.enkarin.bookcrossing.chat.dto.ChatInfo;
import io.github.enkarin.bookcrossing.chat.dto.MessageDto;
import io.github.enkarin.bookcrossing.chat.dto.MessageRequest;
import io.github.enkarin.bookcrossing.chat.dto.UsersCorrKeyDto;
import io.github.enkarin.bookcrossing.chat.service.CorrespondenceService;
import io.github.enkarin.bookcrossing.chat.service.MessageService;
import io.github.enkarin.bookcrossing.constant.ErrorMessage;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

class CorrespondenceControllerTest extends BookCrossingBaseTests {

    @Autowired
    private AdminService adminService;
    @Autowired
    private CorrespondenceService correspondenceService;
    @Autowired
    private MessageService messageService;

    private static final String USER_ID = "userId";
    private static final String FIRST_USER_ID = "firstUserId";
    private static final String SECOND_USER_ID = "secondUserId";

    @Test
    void createCorrespondenceShouldWork() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        final var userAlexId = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();
        enabledUser(userBotId);
        enabledUser(userAlexId);

        final var response = execute(HttpMethod.POST, userAlexId, 201)
            .expectBody(UsersCorrKeyDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .isNotNull()
            .isEqualTo(UsersCorrKeyDto.builder()
                .firstUserId(userBotId)
                .secondUserId(userAlexId)
                .build());
    }

    @Test
    void createCorrespondenceShouldFailWithUserNotFound() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);

        execute(HttpMethod.POST, Integer.MAX_VALUE, 404)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(ErrorMessage.ERROR_1003.getCode());
    }

    @Test
    void createCorrespondenceShouldFailWithUserNotEnabled() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);
        final var userAlexId = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();

        execute(HttpMethod.POST, userAlexId, 406)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(ErrorMessage.ERROR_1011.getCode());
    }

    @Test
    void createCorrespondenceShouldFailWithUserLocked() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        adminService.lockedUser(LockedUserDto.create(userAlex.getLogin(), "Заблокировано"));

        execute(HttpMethod.POST, userAlex.getUserId(), 406)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(ErrorMessage.ERROR_1011.getCode());
    }

    @Test
    void createCorrespondenceShouldFailWithUserLockedAndNotEnabled() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        adminService.lockedUser(LockedUserDto.create(userAlex.getLogin(), "Заблокировано"));

        execute(HttpMethod.POST, userAlex.getUserId(), 406)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(ErrorMessage.ERROR_1011.getCode());
    }

    @Test
    void createCorrespondenceShouldFailWithChatAlreadyCreated() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlexId = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();
        enabledUser(userAlexId);

        correspondenceService.createChat(userAlexId, userBot.getLogin());

        execute(HttpMethod.POST, userAlexId, 409)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(ErrorMessage.ERROR_1010.getCode());
    }

    @Test
    void createCorrespondenceWithoutUserIdShouldReturn400() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        execute(HttpMethod.POST, "", 400).expectBody()
            .jsonPath("$.errorList").isEqualTo("3013");
    }

    @Test
    void getCorrespondenceWithoutFirstUserIdShouldReturn400() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        execute("", "123", 400).expectBody()
            .jsonPath("$.errorList").isEqualTo("3014");
    }

    @Test
    void getCorrespondenceWithoutSecondUserIdShouldReturn400() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        execute("12", "", 400).expectBody()
            .jsonPath("$.errorList").isEqualTo("3015");
    }

    @Test
    void deleteCorrespondenceWithoutUserIdShouldReturn400() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        execute(HttpMethod.DELETE, "", 400).expectBody()
            .jsonPath("$.errorList").isEqualTo("3013");
    }

    @Test
    void deleteCorrespondenceShouldFailWithUserNotFound() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);

        execute(HttpMethod.DELETE, Integer.MAX_VALUE, 404)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(ErrorMessage.ERROR_1003.getCode());
    }

    @Test
    void deleteCorrespondenceShouldFailWithChatNotFound() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);
        final var userAlexId = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();
        enabledUser(userAlexId);

        execute(HttpMethod.DELETE, userAlexId, 404)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(ErrorMessage.ERROR_1009.getCode());
    }

    @Test
    void deleteCorrespondenceShouldWork() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlexId = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();
        enabledUser(userAlexId);
        correspondenceService.createChat(userAlexId, userBot.getLogin());

        execute(HttpMethod.DELETE, userAlexId, 200);

        assertThat(jdbcTemplate.queryForObject("select exists(select * from bookcrossing.t_correspondence where first_user_id=?)", Boolean.class, userBot.getUserId()))
            .isFalse();
    }

    @Test
    void getCorrespondenceShouldWorkWithEmptyMessage() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlexId = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();
        enabledUser(userAlexId);
        correspondenceService.createChat(userAlexId, userBot.getLogin());

        final var response = execute(userBot.getUserId(), userAlexId, 200)
            .expectBodyList(MessageDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .isNotNull()
            .isEmpty();
    }

    @Test
    void getCorrespondenceShouldWorkWithFirstUserMessage() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlexId = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();
        enabledUser(userAlexId);
        final var key = correspondenceService.createChat(userAlexId, userBot.getLogin());
        final var message = messageService.sendMessage(MessageRequest.create(key, "Hi"), userBot.getLogin());

        final var response = execute(userBot.getUserId(), userAlexId, 200)
            .expectBodyList(MessageDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .isNotNull()
            .containsOnly(TestDataProvider.buildMessageDto(userBot.getUserId(), message.getMessageId(), message.getDepartureDate()));
    }

    @Test
    void getCorrespondenceShouldWorkWithSecondUserMessage() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlexId = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();
        enabledUser(userAlexId);
        final var key = correspondenceService.createChat(userAlexId, userBot.getLogin());
        final var message = messageService.sendMessage(MessageRequest.create(key, "Hi"), userBot.getLogin());

        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "correspondence")
                .queryParam("zone", 0)
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 20)
                .build())
            .headers(headers -> {
                headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot()));
                headers.set(FIRST_USER_ID, String.valueOf(userBot.getUserId()));
                headers.set(SECOND_USER_ID, String.valueOf(userAlexId));
            })
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(MessageDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .isNotNull()
            .containsOnly(TestDataProvider.buildMessageDto(userBot.getUserId(), message.getMessageId(), message.getDepartureDate()));
    }

    @Test
    @SneakyThrows
    void getCorrespondenceShouldWorkWithSecondUserMessageWithPagination() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlexId = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();
        enabledUser(userAlexId);
        final var key = correspondenceService.createChat(userAlexId, userBot.getLogin());
        messageService.sendMessage(MessageRequest.create(key, "Hi"), userBot.getLogin());
        Thread.sleep(1000);
        messageService.sendMessage(MessageRequest.create(key, "Hi1"), userBot.getLogin());
        Thread.sleep(1000);
        messageService.sendMessage(MessageRequest.create(key, "Hi2"), userBot.getLogin());
        Thread.sleep(1000);
        messageService.sendMessage(MessageRequest.create(key, "Hi3"), userBot.getLogin());

        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "correspondence")
                .queryParam("zone", 0)
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 2)
                .build())
            .headers(headers -> {
                headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot()));
                headers.set(FIRST_USER_ID, String.valueOf(userBot.getUserId()));
                headers.set(SECOND_USER_ID, String.valueOf(userAlexId));
            })
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(MessageDto.class)
            .returnResult().getResponseBody();

        assertThat(response).satisfies(messagesDto -> {
            assertThat(messagesDto).extracting(MessageDto::isDeclaim).containsOnly(false);
            assertThat(messagesDto).extracting(MessageDto::getText).containsOnly("Hi3", "Hi2");
            assertThat(messagesDto).extracting(MessageDto::getSender).containsOnly(userBot.getUserId());
        });
    }

    @Test
    void getCorrespondenceShouldFailWithNoAccess() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBot);
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        correspondenceService.createChat(userAlex.getUserId(), userAlex.getLogin());

        execute(userAlex.getUserId(), userAlex.getUserId(), 403)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(ErrorMessage.ERROR_1012.getCode());
    }

    @Test
    void getCorrespondenceShouldFailWithFirstUserNotFound() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);

        execute(Integer.MAX_VALUE, userBotId, 404)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(ErrorMessage.ERROR_1003.getCode());
    }

    @Test
    void getCorrespondenceShouldFailWithSecondUserNotFound() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);

        execute(userBotId, Integer.MAX_VALUE, 404)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(ErrorMessage.ERROR_1003.getCode());
    }

    @Test
    void getCorrespondenceShouldFailWithChatNotFound() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);
        final var userAlexId = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();
        enabledUser(userAlexId);

        execute(userBotId, userAlexId, 404)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(ErrorMessage.ERROR_1009.getCode());
    }

    @Test
    @SneakyThrows
    void findAllChats() {
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        correspondenceService.createChat(userAlex.getUserId(), userBot.getLogin());
        messageService.sendMessage(MessageRequest.create(UsersCorrKeyDto.fromFirstAndSecondId(userAlex.getUserId(), userBot.getUserId()), "Hello"), userBot.getLogin());
        Thread.sleep(1000);
        messageService.sendMessage(MessageRequest.create(UsersCorrKeyDto.fromFirstAndSecondId(userAlex.getUserId(), userBot.getUserId()), "))"), userBot.getLogin());
        final var secondBot = createAndSaveUser(TestDataProvider.prepareUser().login("secondUser").email("secondUser@mail.ru").build());
        enabledUser(secondBot.getUserId());
        correspondenceService.createChat(userAlex.getUserId(), secondBot.getLogin());
        messageService.sendMessage(MessageRequest.create(UsersCorrKeyDto.fromFirstAndSecondId(userAlex.getUserId(), secondBot.getUserId()), "Hi"), secondBot.getLogin());
        final var thirdBot = createAndSaveUser(TestDataProvider.buildMax());
        enabledUser(thirdBot.getUserId());
        correspondenceService.createChat(userAlex.getUserId(), thirdBot.getLogin());
        messageService.sendMessage(MessageRequest.create(UsersCorrKeyDto.fromFirstAndSecondId(userAlex.getUserId(), thirdBot.getUserId()), "QQQ"), thirdBot.getLogin());
        Thread.sleep(1000);
        messageService.sendMessage(MessageRequest.create(UsersCorrKeyDto.fromFirstAndSecondId(userAlex.getUserId(), thirdBot.getUserId()), "Q"), thirdBot.getLogin());

        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "correspondence", "all")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 3)
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthAlex())))
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(ChatInfo.class)
            .returnResult().getResponseBody();
        assertThat(response).containsOnly(
            new ChatInfo(userBot.getName(), "))", userBot.getUserId(), userBot.getUserId(), 2),
            new ChatInfo(secondBot.getName(), "Hi", secondBot.getUserId(), secondBot.getUserId(), 1),
            new ChatInfo(thirdBot.getName(), "Q", thirdBot.getUserId(), thirdBot.getUserId(), 2));
    }

    private WebTestClient.ResponseSpec execute(final HttpMethod httpMethod, final int userId, final int status) {
        return execute(httpMethod, Integer.toString(userId), status);
    }

    private WebTestClient.ResponseSpec execute(final HttpMethod httpMethod, final String userId, final int status) {
        return webClient.method(httpMethod)
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "correspondence")
                .build())
            .headers(headers -> {
                headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot()));
                headers.set(USER_ID, userId);
            })
            .exchange()
            .expectStatus().isEqualTo(status);
    }

    private WebTestClient.ResponseSpec execute(final int firstUserId, final int secondUserId, final int status) {
        return execute(Integer.toString(firstUserId), Integer.toString(secondUserId), status);
    }

    private WebTestClient.ResponseSpec execute(final String firstUserId, final String secondUserId, final int status) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "correspondence")
                .queryParam("zone", 0)
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
                .build())
            .headers(headers -> {
                headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot()));
                headers.set(FIRST_USER_ID, firstUserId);
                headers.set(SECOND_USER_ID, secondUserId);
            })
            .exchange()
            .expectStatus().isEqualTo(status);
    }
}
