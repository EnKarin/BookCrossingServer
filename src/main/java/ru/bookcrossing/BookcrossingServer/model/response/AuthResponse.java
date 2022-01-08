package ru.bookcrossing.BookcrossingServer.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuthResponse {

    @Schema(description = "Токен доступа", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJUZXN0IiwiZXhwIjoxNjQwNzk3MjAwfQ")
    private String accessToken;

    @Schema(description = "Токен обновления", example = "cac2ce3e-9ff0-49a7-8afc-3dcae34eafea")
    private String refreshToken;
}
