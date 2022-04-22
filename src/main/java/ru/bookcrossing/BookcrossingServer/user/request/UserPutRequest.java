package ru.bookcrossing.BookcrossingServer.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
@Data
@Schema(description = "Сущность для изменения данных пользователя")
public class UserPutRequest {

    @Schema(description = "Имя", example = "Alex")
    @NotBlank(message = "Имя должно содержать хотя бы один видимый символ")
    private String name;

    @Schema(description = "Старый пароль", example = "123456")
    @NotBlank(message = "Пароль должен содержать хотя бы один видимый символ")
    private String oldPassword;

    @Schema(description = "Новый пароль", example = "123456s", required = true)
    @NotBlank(message = "Пароль должен содержать хотя бы один видимый символ")
    @Size(min = 6, message = "Пароль должен содержать больше 6 символов")
    private String newPassword;

    @Schema(description = "Подвержение пароля", example = "123456s", required = true)
    private String passwordConfirm;

    @Schema(description = "Почта", example = "al@yandex.ru")
    @Email(message = "Некорректный почтовый адрес")
    private String email;

    @Schema(description = "Город", example = "Новосибирск")
    private String city;
}
