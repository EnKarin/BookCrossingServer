package ru.bookcrossing.BookcrossingServer.refresh.service;


import java.util.Optional;

public interface RefreshService {

    String createToken(String login);

    Optional<String> findByToken(String token);
}
