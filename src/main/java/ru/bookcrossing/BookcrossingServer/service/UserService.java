package ru.bookcrossing.BookcrossingServer.service;

import ru.bookcrossing.BookcrossingServer.entity.User;

import java.util.List;

public interface UserService {
    boolean saveUser(User user);

    void deleteUser(Integer userId);

    List<User> usergtList(Integer idMin);

    User findByLogin(String login);

    List<User> findAll();
}
