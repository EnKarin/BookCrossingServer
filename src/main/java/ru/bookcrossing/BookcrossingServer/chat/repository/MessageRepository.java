package ru.bookcrossing.BookcrossingServer.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.BookcrossingServer.chat.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
