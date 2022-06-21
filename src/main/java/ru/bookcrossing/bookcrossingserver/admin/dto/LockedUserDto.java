package ru.bookcrossing.bookcrossingserver.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "Сущностьдля блокировки пользователя")
@Validated
public class LockedUserDto {

    @Schema(description = "Логин", example = "LogAll")
    @NotBlank(message = "login: Логин должен содержать хотя бы один видимый символ")
    private String login;

    @Schema(description = "Комментарий", example = "Неприемлимые комментарии")
    @NotBlank(message = "comment: Комментарий должен содержать хотя бы один видимый символ")
    private String comment;
}
