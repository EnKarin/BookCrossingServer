package io.github.enkarin.bookcrossing.mail.repository;

import io.github.enkarin.bookcrossing.mail.model.ActionMailUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionMailUserRepository extends JpaRepository<ActionMailUser, String> {
}
