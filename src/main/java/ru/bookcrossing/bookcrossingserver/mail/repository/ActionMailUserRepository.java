package ru.bookcrossing.bookcrossingserver.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.bookcrossingserver.mail.model.ActionMailUser;

public interface ActionMailUserRepository extends JpaRepository<ActionMailUser, String> {
}
