package io.github.enkarin.bookcrossing.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;

@Immutable
@SuperBuilder
@EqualsAndHashCode
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "Общие данные пользователя")
public class UserParentDto {

    @Schema(description = "Идентификатор", example = "0")
    protected final int userId;

    @Schema(description = "Имя", example = "Alex")
    protected final String name;

    @Schema(description = "Логин", example = "alex")
    protected final String login;

    @Schema(description = "Почта", example = "al@yandex.ru")
    protected final String email;

    @Schema(description = "Город", example = "Новосибирск")
    protected final String city;

    @Schema(description = "Заблокирован ли аккаунт", example = "true")
    protected final boolean accountNonLocked;

    @Schema(description = "Активирован ли аккаунт", example = "true")
    protected final boolean enabled;

    @Schema(description = "Информация о пользователе")
    protected final String aboutMe;

    public static UserParentDto create(final int userId, final String name, final String login, final String email,
                                       final String city, final boolean accountNonLocked, final boolean enabled, final String aboutMe) {
        return new UserParentDto(userId, name, login, email, city, accountNonLocked, enabled, aboutMe);
    }
}
