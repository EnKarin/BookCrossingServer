package io.github.enkarin.bookcrossing.chat.service;

import io.github.enkarin.bookcrossing.chat.dto.MessageDto;
import io.github.enkarin.bookcrossing.chat.model.Correspondence;
import io.github.enkarin.bookcrossing.chat.model.Message;
import io.github.enkarin.bookcrossing.chat.repository.MessageRepository;
import io.github.enkarin.bookcrossing.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CorrespondenceServiceHelper {
    private final MessageRepository messageRepository;

    @Transactional
    public List<MessageDto> getMessages(final Predicate<Message> rules,
                                        final Correspondence correspondence,
                                        final int pageNumber,
                                        final int pageSize,
                                        final int zone,
                                        final User user) {
        final var messages = correspondence.getMessage();
        final var response = messages.stream()
            .filter(rules)
            .map(m -> MessageDto.fromMessageAndZone(m, zone))
            .sorted(Comparator.comparing(MessageDto::getDepartureDate))
            .skip((long) pageNumber * pageSize)
            .limit(pageSize)
            .toList();
        messages.stream()
            .filter(m -> !user.equals(m.getSender()))
            .filter(Predicate.not(Message::isDeclaim))
            .peek(m -> m.setDeclaim(true))
            .forEach(messageRepository::save);
        return response;
    }
}
