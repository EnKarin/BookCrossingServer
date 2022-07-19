package io.github.enkarin.bookcrossing.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.concurrent.Immutable;

@Immutable
@EqualsAndHashCode
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Общие данные пользователя")
public class UserParentDto {

    @Schema(description = "Идентификатор", example = "0")
    private final int userId;

    @Schema(description = "Имя", example = "Alex")
    private final String name;

    @Schema(description = "Логин", example = "alex")
    private final String login;

    @Schema(description = "Почта", example = "al@yandex.ru")
    private final String email;

    @Schema(description = "Город", example = "Новосибирск")
    private final String city;

    @Schema(description = "Заблокирован ли аккаунт", example = "true")
    private final boolean accountNonLocked;

    @Schema(description = "Активирован ли аккаунт", example = "true")
    private final boolean enabled;

    public static UserParentDto of(final int userId, final String name, final String login, final String email,
                                   final String city, final boolean accountNonLocked, final boolean enabled) {
        return new UserParentDto(userId, name, login, email, city, accountNonLocked, enabled);
    }
}
