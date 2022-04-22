package ru.bookcrossing.BookcrossingServer.user.service;

import ru.bookcrossing.BookcrossingServer.registation.request.LoginRequest;
import ru.bookcrossing.BookcrossingServer.user.dto.UserDTO;
import ru.bookcrossing.BookcrossingServer.user.model.User;
import ru.bookcrossing.BookcrossingServer.user.request.UserPutRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(UserDTO user);

    boolean confirmMail(String token);

    boolean lockedUser(String login);

    boolean nonLockedUser(String login);

    Optional<User> findByLogin(String login);

    Optional<User> findById(int id);

    Optional<User> findByLoginAndPassword(LoginRequest login);

    List<User> findAllUsers();

    Optional<User> putUserInfo(UserPutRequest userPutRequest, String login);
}
