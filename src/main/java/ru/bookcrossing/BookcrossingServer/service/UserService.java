package ru.bookcrossing.BookcrossingServer.service;

import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.model.DTO.UserDTO;
import ru.bookcrossing.BookcrossingServer.model.request.LoginRequest;
import ru.bookcrossing.BookcrossingServer.model.request.UserPutRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<String> saveUser(UserDTO user);

    void deleteUser(String login);

    Optional<User> findByLogin(String login);

    Optional<User> findById(int id);

    Optional<User> findByLoginAndPassword(LoginRequest login);

    List<User> findAllUsers();

    Optional<User> putUserInfo(UserPutRequest userPutRequest, String login);
}
