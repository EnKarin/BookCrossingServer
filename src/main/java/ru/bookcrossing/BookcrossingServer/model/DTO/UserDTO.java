package ru.bookcrossing.BookcrossingServer.model.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
@Getter
@Setter
@Schema(description = "Сущность пользователя")
public class UserDTO {
    @Schema(description = "Имя", example = "Alex")
    @NotBlank(message = "Имя должно содержать хотя бы один видимый символ")
    private String name;

    @Schema(description = "Логин", example = "LogAll")
    @NotBlank(message = "Логин должен содержать хотя бы один видимый символ")
    private String login;

    @Schema(description = "Пароль", example = "123456")
    @NotBlank(message = "Пароль должен содержать хотя бы один видимый символ")
    @Size(min = 6, message = "Пароль должен содержать больше 6 символов")
    private String password;

    @Schema(description = "Подвержение пароля", example = "123456")
    private String passwordConfirm;

    @Schema(description = "Почта", example = "al@yandex.ru")
    @Email(message = "Некорректный почтовый адрес")
    private String email;

    @Schema(description = "Город", example = "Новосибирск")
    private String city;
}
