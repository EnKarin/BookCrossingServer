package io.github.enkarin.bookcrossing.chat.repository;

import io.github.enkarin.bookcrossing.chat.model.Correspondence;
import io.github.enkarin.bookcrossing.chat.model.Message;
import io.github.enkarin.bookcrossing.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT COUNT(m) FROM Message m WHERE m.correspondence = ?1 AND m.sender != ?2 AND m.declaim = false")
    int countAllUnreadMessageFromSpecifiedChatAndToCurrentUser(Correspondence chat, User user);
}
