package io.github.enkarin.bookcrossing.schedulingtasks;

import io.github.enkarin.bookcrossing.chat.model.Message;
import io.github.enkarin.bookcrossing.mail.service.MailService;
import io.github.enkarin.bookcrossing.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageAlertsService {
    private final MailService mailService;
    private final MessageAlertsServiceHelper messageAlertsServiceHelper;

    @Scheduled(cron = "0 */30 * * * *")
    public void sendAlerts() {
        final Map<User, Integer> map = new HashMap<>();
        final List<Message> unread = messageAlertsServiceHelper.findUnreadMessages();
        if (!unread.isEmpty()) {
            User user;
            for (final Message message : unread) {
                user = message.getCorrespondence().getUsersCorrKey().getFirstUser();
                if (user.equals(message.getSender())) {
                    user = message.getCorrespondence().getUsersCorrKey().getSecondUser();
                    if (!user.equals(message.getSender())) {
                        map.putIfAbsent(user, 0);
                        map.put(user, map.get(user) + 1);
                    }
                } else {
                    map.putIfAbsent(user, 0);
                    map.put(user, map.get(user) + 1);
                }
            }
            for (final Map.Entry<User, Integer> entry : map.entrySet()) {
                log.info("Sending alert for userId {}", entry.getKey().getUserId());
                mailService.sendAlertsMessage(entry.getKey(), entry.getValue());
            }
            messageAlertsServiceHelper.setAlertSent(unread);
        }
    }
}
