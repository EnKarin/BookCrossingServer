package io.github.enkarin.bookcrossing.chat.service;

import io.github.enkarin.bookcrossing.chat.dto.ChatInfo;
import io.github.enkarin.bookcrossing.chat.dto.MessageRequest;
import io.github.enkarin.bookcrossing.chat.dto.UsersCorrKeyDto;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class FindChatsServiceTest extends BookCrossingBaseTests {
    @Autowired
    private CorrespondenceService correspondenceService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private FindChatsService findChatsService;

    @Test
    void findAllChats() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        correspondenceService.createChat(userAlex.getUserId(), userBot.getLogin());
        messageService.sendMessage(MessageRequest.create(UsersCorrKeyDto.fromFirstAndSecondId(userBot.getUserId(), userAlex.getUserId()), "Hello"), userBot.getLogin());

        assertThat(findChatsService.findAllChats(0, 5, userBot.getLogin()))
            .contains(new ChatInfo(userAlex.getName(), "Hello", userBot.getUserId(), userAlex.getUserId(), 0));
    }

    @Test
    void findAllChatsFromNotReadMessageUser() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        correspondenceService.createChat(userAlex.getUserId(), userBot.getLogin());
        messageService.sendMessage(MessageRequest.create(UsersCorrKeyDto.fromFirstAndSecondId(userBot.getUserId(), userAlex.getUserId()), "Hello"), userBot.getLogin());

        assertThat(findChatsService.findAllChats(0, 5, userAlex.getLogin()))
            .contains(new ChatInfo(userBot.getName(), "Hello", userBot.getUserId(), userBot.getUserId(), 1));
    }

    @Test
    void findAllChatsWithSwapKeys() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        correspondenceService.createChat(userAlex.getUserId(), userBot.getLogin());
        messageService.sendMessage(MessageRequest.create(UsersCorrKeyDto.fromFirstAndSecondId(userAlex.getUserId(), userBot.getUserId()), "Hello"), userBot.getLogin());

        assertThat(findChatsService.findAllChats(0, 5, userBot.getLogin()))
            .contains(new ChatInfo(userAlex.getName(), "Hello", userBot.getUserId(), userAlex.getUserId(), 0));
    }

    @Test
    void findAllChatsFromEmptyChat() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        correspondenceService.createChat(userAlex.getUserId(), userBot.getLogin());

        assertThat(findChatsService.findAllChats(0, 5, userBot.getLogin()))
            .contains(new ChatInfo(userAlex.getName(), null, null, userAlex.getUserId(), 0));
    }

    @Test
    @SneakyThrows
    void findAllChatsWithPageSize() {
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        final UserDto[] userDto = configureSomeChats(userAlex);

        assertThat(findChatsService.findAllChats(0, 2, userAlex.getLogin())).containsOnly(
            new ChatInfo(userDto[0].getName(), "))", userDto[0].getUserId(), userDto[0].getUserId(), 2),
            new ChatInfo(userDto[1].getName(), "Hi", userDto[1].getUserId(), userDto[1].getUserId(), 1));
    }

    @Test
    @SneakyThrows
    void findAllChatsWithPageNumber() {
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(userAlex.getUserId());
        final UserDto[] userDto = configureSomeChats(userAlex);

        assertThat(findChatsService.findAllChats(1, 2, userAlex.getLogin()))
            .containsOnly(new ChatInfo(userDto[2].getName(), "Q", userDto[2].getUserId(), userDto[2].getUserId(), 2));
    }

    private UserDto[] configureSomeChats(final UserDto mainUser) throws InterruptedException {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        correspondenceService.createChat(mainUser.getUserId(), userBot.getLogin());
        messageService.sendMessage(MessageRequest.create(UsersCorrKeyDto.fromFirstAndSecondId(mainUser.getUserId(), userBot.getUserId()), "Hello"), userBot.getLogin());
        Thread.sleep(1000);
        messageService.sendMessage(MessageRequest.create(UsersCorrKeyDto.fromFirstAndSecondId(mainUser.getUserId(), userBot.getUserId()), "))"), userBot.getLogin());
        final var secondBot = createAndSaveUser(TestDataProvider.prepareUser().login("secondUser").email("secondUser@mail.ru").build());
        enabledUser(secondBot.getUserId());
        correspondenceService.createChat(mainUser.getUserId(), secondBot.getLogin());
        messageService.sendMessage(MessageRequest.create(UsersCorrKeyDto.fromFirstAndSecondId(mainUser.getUserId(), secondBot.getUserId()), "Hi"), secondBot.getLogin());
        final var thirdBot = createAndSaveUser(TestDataProvider.buildMax());
        enabledUser(thirdBot.getUserId());
        correspondenceService.createChat(mainUser.getUserId(), thirdBot.getLogin());
        messageService.sendMessage(MessageRequest.create(UsersCorrKeyDto.fromFirstAndSecondId(mainUser.getUserId(), thirdBot.getUserId()), "QQQ"), thirdBot.getLogin());
        Thread.sleep(1000);
        messageService.sendMessage(MessageRequest.create(UsersCorrKeyDto.fromFirstAndSecondId(mainUser.getUserId(), thirdBot.getUserId()), "Q"), thirdBot.getLogin());
        return new UserDto[]{userBot, secondBot, thirdBot};
    }
}