package io.github.enkarin.bookcrossing.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Validated
@Schema(description = "Сущность для смены пароля")
public class UserPasswordDto {

    @Schema(description = "Пароль", example = "123456")
    @NotBlank(message = "password: Пароль должен содержать хотя бы один видимый символ")
    @Size(min = 6, message = "Пароль должен содержать больше 6 символов")
    private final String password;

    @Schema(description = "Подвержение пароля", example = "123456", required = true)
    private final String passwordConfirm;

    private UserPasswordDto(final String password, final String passwordConfirm) {
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }

    @JsonCreator
    public static UserPasswordDto create(final String password, final String passwordConfirm) {
        return new UserPasswordDto(password, passwordConfirm);
    }
}
