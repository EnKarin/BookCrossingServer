package ru.bookcrossing.BookcrossingServer.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.BookcrossingServer.books.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, String> {
}
