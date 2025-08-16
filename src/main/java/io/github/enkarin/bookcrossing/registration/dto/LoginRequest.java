package io.github.enkarin.bookcrossing.registration.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Immutable
@Getter
@EqualsAndHashCode
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginRequest {

    @Schema(description = "Логин", example = "LogAll")
    @NotBlank
    private final String login;

    @Schema(description = "Пароль", example = "123456")
    @Size(min = 6, message = "Пароль слишком короткий")
    private final String password;

    @Schema(description = "Часовой пояс пользователя", example = "7")
    private final int zone;

    @JsonCreator
    public static LoginRequest create(final String login, final String password, final int zone) {
        return new LoginRequest(login, password, zone);
    }
}
