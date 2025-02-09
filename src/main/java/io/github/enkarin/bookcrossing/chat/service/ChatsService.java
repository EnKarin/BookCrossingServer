package io.github.enkarin.bookcrossing.chat.service;

import io.github.enkarin.bookcrossing.chat.dto.ChatInfo;
import io.github.enkarin.bookcrossing.chat.model.Message;
import io.github.enkarin.bookcrossing.chat.repository.CorrespondenceRepository;
import io.github.enkarin.bookcrossing.exception.MessageNotFountException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatsService {
    private final CorrespondenceRepository correspondenceRepository;
    private final UserRepository userRepository;

    public ChatInfo[] findAllChats(final int pageNumber, final int pageSize, String login) {
        final User currentUser = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        return correspondenceRepository.findAllByUser(currentUser, PageRequest.of(pageNumber, pageSize)).stream()
            .map(correspondence -> {
                final Message lastMessage = correspondence.getMessage().stream().max(Comparator.comparing(Message::getDepartureDate)).orElseThrow(MessageNotFountException::new);
                final User interlocutor = correspondence.getUsersCorrKey().getFirstUser().equals(currentUser)
                    ? correspondence.getUsersCorrKey().getSecondUser()
                    : correspondence.getUsersCorrKey().getFirstUser();
                return new ChatInfo(interlocutor.getName(), lastMessage.getText(), lastMessage.getSender().getUserId(), interlocutor.getUserId());
            })
            .toArray(ChatInfo[]::new);
    }
}
