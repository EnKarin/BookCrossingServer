package ru.bookcrossing.BookcrossingServer.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
@Getter
@Setter
public class Login {

    @NotBlank
    private String username;

    @Size(min = 6, message = "Пароль слишком короткий")
    private String password;
}
