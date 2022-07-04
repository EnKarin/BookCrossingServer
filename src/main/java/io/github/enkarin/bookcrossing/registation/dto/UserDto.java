package io.github.enkarin.bookcrossing.registation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
@Getter
@Schema(description = "Сущность пользователя")
public class UserDto {

    @Schema(description = "Имя", example = "Alex")
    @NotBlank(message = "name: Имя должно содержать хотя бы один видимый символ")
    private final String name;

    @Schema(description = "Логин", example = "LogAll")
    @NotBlank(message = "login: Логин должен содержать хотя бы один видимый символ")
    private final String login;

    @Schema(description = "Пароль", example = "123456")
    @NotBlank(message = "password: Пароль должен содержать хотя бы один видимый символ")
    @Size(min = 6, message = "Пароль должен содержать больше 6 символов")
    private final String password;

    @Schema(description = "Подвержение пароля", example = "123456", required = true)
    private final String passwordConfirm;

    @Schema(description = "Почта", example = "al@yandex.ru")
    @Email(message = "email: Некорректный почтовый адрес")
    private final String email;

    @Schema(description = "Город", example = "Новосибирск")
    private final String city;

    private UserDto(String name, String login, String password, String passwordConfirm, String email, String city) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.email = email;
        this.city = city;
    }

    @JsonCreator
    public static UserDto create(String name, String login, String password, String passwordConfirm, String email,
                          String city) {
        return new UserDto(name, login, password, passwordConfirm, email, city);
    }
}
