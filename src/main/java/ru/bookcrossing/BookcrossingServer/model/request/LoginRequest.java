package ru.bookcrossing.BookcrossingServer.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
@Data
public class LoginRequest {

    @Schema(description = "Логин", example = "LogAll")
    @NotBlank
    private String login;

    @Schema(description = "Пароль", example = "123456")
    @Size(min = 6, message = "Пароль слишком короткий")
    private String password;
}
