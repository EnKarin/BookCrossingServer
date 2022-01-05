package ru.bookcrossing.BookcrossingServer.model;

import lombok.Data;

@Data
public class AuthResponse {

    private String accessToken;

    private String refreshToken;
}
