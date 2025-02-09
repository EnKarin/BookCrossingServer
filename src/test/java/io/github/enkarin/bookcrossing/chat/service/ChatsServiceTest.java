package io.github.enkarin.bookcrossing.chat.service;

import io.github.enkarin.bookcrossing.chat.dto.ChatInfo;
import io.github.enkarin.bookcrossing.chat.dto.MessageRequest;
import io.github.enkarin.bookcrossing.chat.dto.UsersCorrKeyDto;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ChatsServiceTest extends BookCrossingBaseTests {
    @Autowired
    private CorrespondenceService correspondenceService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatsService chatsService;

    @Test
    void findAllChats() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        correspondenceService.createChat(userAlex.getUserId(), userBot.getLogin());
        messageService.sendMessage(MessageRequest.create(UsersCorrKeyDto.fromFirstAndSecondId(userAlex.getUserId(), userBot.getUserId()), "Hello"), userBot.getLogin());

        assertThat(chatsService.findAllChats(0, 5, userBot.getLogin())).contains(new ChatInfo(userAlex.getName(), "Hello"));
    }
}