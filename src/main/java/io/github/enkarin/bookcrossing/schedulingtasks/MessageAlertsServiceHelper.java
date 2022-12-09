package io.github.enkarin.bookcrossing.schedulingtasks;

import io.github.enkarin.bookcrossing.chat.model.Message;
import io.github.enkarin.bookcrossing.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageAlertsServiceHelper {

    private final MessageRepository messageRepository;

    public List<Message> findUnreadMessages() {
        return messageRepository.findAll().stream()
                .filter(Predicate.not(Message::isDeclaim))
                .filter(Predicate.not(Message::isAlertSent))
                .filter(m -> m.getCorrespondence().getUsersCorrKey().getFirstUser().isAccountNonLocked())
                .filter(m -> m.getCorrespondence().getUsersCorrKey().getSecondUser().isAccountNonLocked())
                .filter(m -> m.getCorrespondence().getUsersCorrKey().getFirstUser().isEnabled())
                .filter(m -> m.getCorrespondence().getUsersCorrKey().getSecondUser().isEnabled())
                .toList();
    }

    @Transactional
    public void setAlertSent(final List<Message> unread) {
        unread.forEach(m -> m.setAlertSent(true));
        messageRepository.saveAllAndFlush(unread);
    }
}
