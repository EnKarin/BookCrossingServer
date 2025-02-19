package io.github.enkarin.bookcrossing.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.Size;

@SuperBuilder
@Immutable
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Сущность для изменения данных пользователя", requiredProperties = {"newPassword", "passwordConfirm"})
public class UserPutProfileDto {

    @Schema(description = "Имя", example = "Alex")
    private final String name;

    @Schema(description = "Старый пароль", example = "123456")
    private final String oldPassword;

    @Schema(description = "Новый пароль", example = "123456s")
    @Size(min = 8, message = "3010")
    private final String newPassword;

    @Schema(description = "Подвержение пароля", example = "123456s")
    private final String passwordConfirm;

    @Schema(description = "Город", example = "Новосибирск")
    private final String city;

    @JsonCreator
    public static UserPutProfileDto create(final String name, final String oldPassword, final String newPassword,
                                           final String passwordConfirm, final String city) {
        return new UserPutProfileDto(name, oldPassword, newPassword, passwordConfirm, city);
    }
}
