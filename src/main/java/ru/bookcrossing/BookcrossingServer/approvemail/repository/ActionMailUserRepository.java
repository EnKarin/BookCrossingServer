package ru.bookcrossing.BookcrossingServer.approvemail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.BookcrossingServer.approvemail.model.ActionMailUser;

public interface ActionMailUserRepository extends JpaRepository<ActionMailUser, String> {
}
