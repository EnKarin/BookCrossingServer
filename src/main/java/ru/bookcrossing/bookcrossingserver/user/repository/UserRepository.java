package ru.bookcrossing.bookcrossingserver.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.bookcrossingserver.user.model.Role;
import ru.bookcrossing.bookcrossingserver.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLogin(String login);

    Optional<User> findByEmail(String email);

    List<User> findByUserRoles(Role userRoles);
}
