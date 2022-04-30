package ru.bookcrossing.BookcrossingServer.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.BookcrossingServer.mail.model.ActionMailUser;

public interface ActionMailUserRepository extends JpaRepository<ActionMailUser, String> {
}
