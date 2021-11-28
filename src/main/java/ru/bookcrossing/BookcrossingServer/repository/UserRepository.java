package ru.bookcrossing.BookcrossingServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.BookcrossingServer.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByLogin(String login);
}
