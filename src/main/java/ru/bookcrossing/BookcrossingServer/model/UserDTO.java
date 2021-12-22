package ru.bookcrossing.BookcrossingServer.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
@Getter
@Setter
public class UserDTO {
    @NotBlank(message = "Имя должно содержать хотя бы один видимый символ")
    private String name;

    @NotBlank(message = "Логин должен содержать хотя бы один видимый символ")
    private String login;

    @NotBlank(message = "Пароль должен содержать хотя бы один видимый символ")
    @Size(min = 6, message = "Пароль должен содержать больше 6 символов")
    private String password;

    private String passwordConfirm;

    @Email(message = "Некорректный почтовый адрес")
    private String email;

    private String city;
}
