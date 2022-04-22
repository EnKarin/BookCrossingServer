package ru.bookcrossing.BookcrossingServer.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.BookcrossingServer.mail.model.ConfirmationMailUser;

public interface ConfirmationMailUserRepository extends JpaRepository<ConfirmationMailUser, String> {
}
