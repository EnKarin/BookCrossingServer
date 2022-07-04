package io.github.enkarin.bookcrossing.registation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
@Getter
public class LoginRequest {

    @Schema(description = "Логин", example = "LogAll")
    @NotBlank
    private final String login;

    @Schema(description = "Пароль", example = "123456")
    @Size(min = 6, message = "Пароль слишком короткий")
    private final String password;

    @Schema(description = "Часовой пояс пользователя", example = "7")
    private final int zone;

    private LoginRequest(final String login, final String password, final int zone) {
        this.login = login;
        this.password = password;
        this.zone = zone;
    }

    @JsonCreator
    public static LoginRequest create(final String login, final String password, final int zone) {
        return new LoginRequest(login, password, zone);
    }
}
