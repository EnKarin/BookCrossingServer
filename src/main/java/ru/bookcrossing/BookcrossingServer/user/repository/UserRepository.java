package ru.bookcrossing.BookcrossingServer.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.BookcrossingServer.user.model.Role;
import ru.bookcrossing.BookcrossingServer.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByLogin(String login);

    User findByEmail(String email);

    List<User> findByUserRoles(Role userRoles);

    void deleteByLogin(String login);
}
