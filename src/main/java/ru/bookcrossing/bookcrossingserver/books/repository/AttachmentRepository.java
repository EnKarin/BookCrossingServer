package ru.bookcrossing.bookcrossingserver.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.bookcrossingserver.books.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, String> {
}
