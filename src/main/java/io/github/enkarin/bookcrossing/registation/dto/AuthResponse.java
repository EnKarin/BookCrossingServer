package io.github.enkarin.bookcrossing.registation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class AuthResponse {

    @Schema(description = "Токен доступа", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJUZXN0IiwiZXhwIjoxNjQwNzk3MjAwfQ")
    private final String accessToken;

    @Schema(description = "Токен обновления", example = "cac2ce3e-9ff0-49a7-8afc-3dcae34eafea")
    private final String refreshToken;

    private AuthResponse(final String access, final String refresh) {
        accessToken = access;
        refreshToken = refresh;
    }

    public static AuthResponse create(final String access, final String refresh) {
        return new AuthResponse(access, refresh);
    }
}
