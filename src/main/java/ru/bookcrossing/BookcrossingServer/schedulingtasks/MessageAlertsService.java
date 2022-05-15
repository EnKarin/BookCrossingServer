package ru.bookcrossing.BookcrossingServer.schedulingtasks;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.bookcrossing.BookcrossingServer.chat.model.Message;
import ru.bookcrossing.BookcrossingServer.chat.repository.MessageRepository;
import ru.bookcrossing.BookcrossingServer.mail.service.MailService;
import ru.bookcrossing.BookcrossingServer.user.model.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageAlertsService {

    private final MessageRepository messageRepository;
    private final MailService mailService;

    private static final Logger log = LoggerFactory.getLogger(MessageAlertsService.class);

    @Scheduled(cron = "0 */30 * * * *")
    public void sendAlerts(){
        Map<User, Integer> map = new HashMap<>();
        List<Message> unread = messageRepository.findAll().stream()
                .filter(Predicate.not(Message::isDeclaim))
                .filter(Predicate.not(Message::isAlertSent))
                .filter(m -> m.getCorrespondence().getUsersCorrKey().getFirstUser().isAccountNonLocked())
                .filter(m -> m.getCorrespondence().getUsersCorrKey().getSecondUser().isAccountNonLocked())
                .filter(m -> m.getCorrespondence().getUsersCorrKey().getFirstUser().isEnabled())
                .filter(m -> m.getCorrespondence().getUsersCorrKey().getSecondUser().isEnabled())
                .collect(Collectors.toList());
        if(!unread.isEmpty()) {
            User user;
            for (Message message : unread) {
                user = message.getCorrespondence().getUsersCorrKey().getFirstUser();
                if (!user.equals(message.getSender())) {
                    map.putIfAbsent(user, 0);
                    map.put(user, map.get(user) + 1);
                } else {
                    user = message.getCorrespondence().getUsersCorrKey().getSecondUser();
                    if (!user.equals(message.getSender())) {
                        map.putIfAbsent(user, 0);
                        map.put(user, map.get(user) + 1);
                    }
                }
            }
            for (Map.Entry<User, Integer> entry : map.entrySet()) {
                mailService.sendAlertsMessage(entry.getKey(), entry.getValue());
            }
            unread.forEach(m -> {
                m.setAlertSent(true);
                messageRepository.save(m);
            });
        }
        log.info("The time is now {}", LocalDateTime.now());
    }
}
