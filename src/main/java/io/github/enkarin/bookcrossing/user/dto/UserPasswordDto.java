package io.github.enkarin.bookcrossing.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Immutable
@SuperBuilder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Сущность для смены пароля", requiredProperties = "passwordConfirm")
public class UserPasswordDto {

    @Schema(description = "Пароль", example = "123456")
    @NotBlank(message = "password: Пароль должен содержать хотя бы один видимый символ")
    @Size(min = 6, message = "Пароль должен содержать больше 6 символов")
    private final String password;

    @Schema(description = "Подвержение пароля", example = "123456")
    private final String passwordConfirm;

    @JsonCreator
    public static UserPasswordDto create(final String password, final String passwordConfirm) {
        return new UserPasswordDto(password, passwordConfirm);
    }
}
