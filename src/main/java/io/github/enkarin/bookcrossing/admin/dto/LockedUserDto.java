package io.github.enkarin.bookcrossing.admin.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;
import jakarta.validation.constraints.NotBlank;

@SuperBuilder
@Immutable
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Сущность для блокировки пользователя")
public class LockedUserDto {

    @Schema(description = "Логин", example = "LogAll")
    @NotBlank(message = "login: Логин должен содержать хотя бы один видимый символ")
    private final String login;

    @Schema(description = "Комментарий", example = "Неприемлимые комментарии")
    @NotBlank(message = "comment: Комментарий должен содержать хотя бы один видимый символ")
    private final String comment;

    @JsonCreator
    public static LockedUserDto create(final String login, final String comment) {
        return new LockedUserDto(login, comment);
    }
}
