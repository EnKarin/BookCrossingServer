package ru.bookcrossing.BookcrossingServer.service;

import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.model.Login;
import ru.bookcrossing.BookcrossingServer.model.DTO.UserDTO;

import java.util.List;

public interface UserService {
    boolean saveUser(UserDTO user);

    void deleteUser(Integer userId);

    User findByLogin(String login);

    User findByLoginAndPassword(Login login);

    List<User> findAll();
}
