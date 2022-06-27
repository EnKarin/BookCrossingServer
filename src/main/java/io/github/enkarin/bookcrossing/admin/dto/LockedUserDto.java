package io.github.enkarin.bookcrossing.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Schema(description = "Сущностьдля блокировки пользователя")
@Validated
public class LockedUserDto {

    @Schema(description = "Логин", example = "LogAll")
    @NotBlank(message = "login: Логин должен содержать хотя бы один видимый символ")
    private final String login;

    @Schema(description = "Комментарий", example = "Неприемлимые комментарии")
    @NotBlank(message = "comment: Комментарий должен содержать хотя бы один видимый символ")
    private final String comment;

    public LockedUserDto(final String login, final String comment) {
        this.login = login;
        this.comment = comment;
    }
}
