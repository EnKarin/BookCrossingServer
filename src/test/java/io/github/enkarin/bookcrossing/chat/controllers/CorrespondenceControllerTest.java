package io.github.enkarin.bookcrossing.chat.controllers;

import io.github.enkarin.bookcrossing.admin.dto.LockedUserDto;
import io.github.enkarin.bookcrossing.admin.service.AdminService;
import io.github.enkarin.bookcrossing.chat.dto.MessageDto;
import io.github.enkarin.bookcrossing.chat.dto.MessageRequest;
import io.github.enkarin.bookcrossing.chat.dto.UsersCorrKeyDto;
import io.github.enkarin.bookcrossing.chat.service.CorrespondenceService;
import io.github.enkarin.bookcrossing.chat.service.MessageService;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
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
            .jsonPath("$.user")
            .isEqualTo("Пользователь не найден");
    }

    @Test
    void createCorrespondenceShouldFailWithUserNotEnabled() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);
        final var userAlexId = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();

        execute(HttpMethod.POST, userAlexId, 406)
            .expectBody()
            .jsonPath("$.user")
            .isEqualTo("С выбранным пользователем нельзя создать чат");
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
            .jsonPath("$.user")
            .isEqualTo("С выбранным пользователем нельзя создать чат");
    }

    @Test
    void createCorrespondenceShouldFailWithUserLockedAndNotEnabled() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        adminService.lockedUser(LockedUserDto.create(userAlex.getLogin(), "Заблокировано"));

        execute(HttpMethod.POST, userAlex.getUserId(), 406)
            .expectBody()
            .jsonPath("$.user")
            .isEqualTo("С выбранным пользователем нельзя создать чат");
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
            .jsonPath("$.correspondence")
            .isEqualTo("Чат с пользователем уже существует");
    }

    @Test
    void createCorrespondenceWithoutUserIdShouldReturn400() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        execute(HttpMethod.POST, "", 400).expectBody()
            .jsonPath("$.userId").isEqualTo("не должно быть пустым");
    }

    @Test
    void getCorrespondenceWithoutFirstUserIdShouldReturn400() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        execute("", "123", 400).expectBody()
            .jsonPath("$.firstUserId").isEqualTo("не должно быть пустым");
    }

    @Test
    void getCorrespondenceWithoutSecondUserIdShouldReturn400() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        execute("12", "", 400).expectBody()
            .jsonPath("$.secondUserId").isEqualTo("не должно быть пустым");
    }

    @Test
    void deleteCorrespondenceWithoutUserIdShouldReturn400() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        execute(HttpMethod.DELETE, "", 400).expectBody()
            .jsonPath("$.userId").isEqualTo("не должно быть пустым");
    }

    @Test
    void deleteCorrespondenceShouldFailWithUserNotFound() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);

        execute(HttpMethod.DELETE, Integer.MAX_VALUE, 404)
            .expectBody()
            .jsonPath("$.user")
            .isEqualTo("Пользователь не найден");
    }

    @Test
    void deleteCorrespondenceShouldFailWithChatNotFound() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);
        final var userAlexId = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();
        enabledUser(userAlexId);

        execute(HttpMethod.DELETE, userAlexId, 404)
            .expectBody()
            .jsonPath("$.correspondence")
            .isEqualTo("Чата не существует");
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
    void getCorrespondenceShouldFailWithNoAccess() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBot);
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        correspondenceService.createChat(userAlex.getUserId(), userAlex.getLogin());

        execute(userAlex.getUserId(), userAlex.getUserId(), 403)
            .expectBody()
            .jsonPath("$.correspondence")
            .isEqualTo("Нет доступа к чату");
    }

    @Test
    void getCorrespondenceShouldFailWithFirstUserNotFound() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);

        execute(Integer.MAX_VALUE, userBotId, 404)
            .expectBody()
            .jsonPath("$.user").isEqualTo("Пользователь не найден");
    }

    @Test
    void getCorrespondenceShouldFailWithSecondUserNotFound() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);

        execute(userBotId, Integer.MAX_VALUE, 404)
            .expectBody()
            .jsonPath("$.user").isEqualTo("Пользователь не найден");
    }

    @Test
    void getCorrespondenceShouldFailWithChatNotFound() {
        final var userBotId = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(userBotId);
        final var userAlexId = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();
        enabledUser(userAlexId);

        execute(userBotId, userAlexId, 404)
            .expectBody()
            .jsonPath("$.correspondence").isEqualTo("Чата не существует");
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
