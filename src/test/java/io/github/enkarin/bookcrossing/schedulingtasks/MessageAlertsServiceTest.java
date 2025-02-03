package io.github.enkarin.bookcrossing.schedulingtasks;

import io.github.enkarin.bookcrossing.admin.dto.LockedUserDto;
import io.github.enkarin.bookcrossing.admin.service.AdminService;
import io.github.enkarin.bookcrossing.chat.model.Message;
import io.github.enkarin.bookcrossing.chat.repository.MessageRepository;
import io.github.enkarin.bookcrossing.chat.service.CorrespondenceService;
import io.github.enkarin.bookcrossing.chat.service.MessageService;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class MessageAlertsServiceTest extends BookCrossingBaseTests {

    @Autowired
    private MessageAlertsService messageAlertsService;
    @Autowired
    private CorrespondenceService correspondenceService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private MessageRepository messageRepository;

    @Test
    void sendAlertsShouldUpdateIsAlertAndSendLetterForFirstUser(final CapturedOutput output) {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        final var messageId = createChatAndMessage(userBot, userAlex);

        messageAlertsService.sendAlerts();

        assertThat(messageRepository.findById(messageId))
            .isPresent()
            .get()
            .extracting(Message::isAlertSent)
            .isEqualTo(true);
        assertThat(output)
            .contains(String.format("Sending alert for userId %d", userAlex.getUserId()));
    }

    @Test
    void sendAlertsShouldUpdateIsAlertAndSendLetterForSecondUser(final CapturedOutput output) {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        final var key = correspondenceService.createChat(userAlex.getUserId(), userBot.getLogin());
        final var messageId = messageService.sendMessage(TestDataProvider.buildMessageRequest(key), userBot.getLogin()).getMessageId();

        messageAlertsService.sendAlerts();

        assertThat(messageRepository.findById(messageId))
            .isPresent()
            .get()
            .extracting(Message::isAlertSent)
            .isEqualTo(true);
        assertThat(output)
            .contains(String.format("Sending alert for userId %d", userAlex.getUserId()));
    }

    @Test
    void sendAlertsShouldNotUpdateIsAlertWithMessageIsDeclaimTrue() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        final var key = correspondenceService.createChat(userBot.getUserId(), userAlex.getLogin());
        final var messageId = messageService.sendMessage(TestDataProvider.buildMessageRequest(key), userBot.getLogin()).getMessageId();
        jdbcTemplate.update("update bookcrossing.t_messages set declaim = true where message_id = " + messageId);

        messageAlertsService.sendAlerts();

        assertThat(messageRepository.findById(messageId))
            .isPresent()
            .get()
            .extracting(Message::isAlertSent)
            .isEqualTo(false);
    }

    @Test
    void sendAlertsShouldNotSendLetterWithIsAlertTrue(final CapturedOutput output) {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        final var messageId = createChatAndMessage(userBot, userAlex);

        jdbcTemplate.update("update  bookcrossing.t_messages set alert_sent = true where message_id = " + messageId);

        messageAlertsService.sendAlerts();

        assertThat(output)
            .doesNotContain(String.format("Sending alert for userId %d", userAlex.getUserId()));
    }

    @Test
    void sendAlertShouldNotSendLetterWithUserBlock(final CapturedOutput output) {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        createChatAndMessage(userBot, userAlex);

        adminService.lockedUser(LockedUserDto.create("Bot", "..."));
        messageAlertsService.sendAlerts();

        adminService.nonLockedUser("Bot");
        adminService.lockedUser(LockedUserDto.create("Alex", "..."));
        messageAlertsService.sendAlerts();

        assertThat(output)
            .doesNotContain(String.format("Sending alert for userId %d", userAlex.getUserId()));
    }

    @Test
    void sendAlertShouldNotSendLetterWithUserNotEnabled(final CapturedOutput output) {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        createChatAndMessage(userBot, userAlex);

        jdbcTemplate.update("update  bookcrossing.t_user set enabled = false where user_id = " + userBot.getUserId());
        messageAlertsService.sendAlerts();

        enabledUser(userBot.getUserId());
        jdbcTemplate.update("update  bookcrossing.t_user set enabled = false where user_id = " + userAlex.getUserId());
        messageAlertsService.sendAlerts();

        assertThat(output)
            .doesNotContain(String.format("Sending alert for userId %d", userAlex.getUserId()));
    }

    private long createChatAndMessage(final UserDto userBot, final UserDto userAlex) {
        final var key = correspondenceService.createChat(userBot.getUserId(), userAlex.getLogin());
        return messageService.sendMessage(TestDataProvider.buildMessageRequest(key), userBot.getLogin()).getMessageId();
    }
}
