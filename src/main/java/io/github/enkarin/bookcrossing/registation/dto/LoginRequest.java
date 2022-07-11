package io.github.enkarin.bookcrossing.registation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
@Value
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginRequest {

    @Schema(description = "Логин", example = "LogAll")
    @NotBlank
    String login;

    @Schema(description = "Пароль", example = "123456")
    @Size(min = 6, message = "Пароль слишком короткий")
    String password;

    @Schema(description = "Часовой пояс пользователя", example = "7")
    int zone;

    @JsonCreator
    public static LoginRequest create(final String login, final String password, final int zone) {
        return new LoginRequest(login, password, zone);
    }
}
