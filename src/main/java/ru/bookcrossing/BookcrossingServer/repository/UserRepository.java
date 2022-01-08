package ru.bookcrossing.BookcrossingServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.BookcrossingServer.entity.Role;
import ru.bookcrossing.BookcrossingServer.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByLogin(String login);

    List<User> findByUserRoles(Role userRoles);

    void deleteByLogin(String login);
}
