package ru.bookcrossing.bookcrossingserver.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.bookcrossingserver.chat.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
