package ru.bookcrossing.BookcrossingServer.model.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.bookcrossing.BookcrossingServer.entity.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
@Data
@Schema(description = "Сущность пользователя")
@NoArgsConstructor
public class UserDTO {
    @Schema(description = "Имя", example = "Alex")
    @NotBlank(message = "name: Имя должно содержать хотя бы один видимый символ")
    private String name;

    @Schema(description = "Логин", example = "LogAll")
    @NotBlank(message = "login: Логин должен содержать хотя бы один видимый символ")
    private String login;

    @Schema(description = "Пароль", example = "123456")
    @NotBlank(message = "password: Пароль должен содержать хотя бы один видимый символ")
    @Size(min = 6, message = "Пароль должен содержать больше 6 символов")
    private String password;

    @Schema(description = "Подвержение пароля", example = "123456", required = true)
    private String passwordConfirm;

    @Schema(description = "Почта", example = "al@yandex.ru")
    @Email(message = "email: Некорректный почтовый адрес")
    private String email;

    @Schema(description = "Город", example = "Новосибирск")
    private String city;

    public UserDTO(User user){
        name = user.getName();
        login = user.getLogin();
        email = user.getEmail();
        city = user.getCity();
    }
}
