package io.github.enkarin.bookcrossing.registration.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@EqualsAndHashCode
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Сущность пользователя", requiredProperties = {"password, passwordConfirm"})
public class UserRegistrationDto {

    @Schema(description = "Имя", example = "Alex")
    @NotBlank(message = "3008")
    private final String name;

    @Schema(description = "Логин", example = "LogAll")
    @Setter
    private String login;

    @Schema(description = "Пароль", example = "123456")
    @NotBlank(message = "3009")
    @Size(min = 8, message = "3010")
    private final String password;

    @Schema(description = "Подвержение пароля", example = "123456")
    private final String passwordConfirm;

    @Schema(description = "Почта", example = "al@yandex.ru")
    @Email(message = "3011")
    private final String email;

    @Schema(description = "Город", example = "Новосибирск")
    private final String city;

    @Schema(description = "Информация о пользователе")
    protected final String aboutMe;

    @JsonCreator
    public static UserRegistrationDto create(final String name, final String login, final String password,
                                             final String passwordConfirm, final String email, final String city, final String aboutMe) {
        return new UserRegistrationDto(name, login, password, passwordConfirm, email, city, aboutMe);
    }
}
