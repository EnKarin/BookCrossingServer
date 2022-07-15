package io.github.enkarin.bookcrossing.registation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Immutable
@Getter
@EqualsAndHashCode
@SuperBuilder
@Validated
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Сущность пользователя")
public class UserRegistrationDto {

    @Schema(description = "Имя", example = "Alex")
    @NotBlank(message = "name: Имя должно содержать хотя бы один видимый символ")
    private final String name;

    @Schema(description = "Логин", example = "LogAll")
    @NotBlank(message = "login: Логин должен содержать хотя бы один видимый символ")
    private final String login;

    @Schema(description = "Пароль", example = "123456")
    @NotBlank(message = "password: Пароль должен содержать хотя бы один видимый символ")
    @Size(min = 6, message = "password: Пароль должен содержать больше 6 символов")
    private final String password;

    @Schema(description = "Подвержение пароля", example = "123456", required = true)
    private final String passwordConfirm;

    @Schema(description = "Почта", example = "al@yandex.ru")
    @Email(message = "email: Некорректный почтовый адрес")
    private final String email;

    @Schema(description = "Город", example = "Новосибирск")
    private final String city;

    @JsonCreator
    public static UserRegistrationDto create(final String name, final String login, final String password,
                                             final String passwordConfirm, final String email, final String city) {
        return new UserRegistrationDto(name, login, password, passwordConfirm, email, city);
    }
}
