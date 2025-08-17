package io.github.enkarin.bookcrossing.registration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.concurrent.Immutable;

@Immutable
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class AuthResponse {

    @Schema(description = "Токен доступа", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJUZXN0IiwiZXhwIjoxNjQwNzk3MjAwfQ")
    private final String accessToken;

    @Schema(description = "Токен обновления", example = "cac2ce3e-9ff0-49a7-8afc-3dcae34eafea")
    private final String refreshToken;

    public static AuthResponse create(final String access, final String refresh) {
        return new AuthResponse(access, refresh);
    }
}
