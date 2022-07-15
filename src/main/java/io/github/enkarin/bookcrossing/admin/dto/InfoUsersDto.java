package io.github.enkarin.bookcrossing.admin.dto;

import io.github.enkarin.bookcrossing.user.dto.UserDto;
import io.github.enkarin.bookcrossing.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.concurrent.Immutable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Immutable
@EqualsAndHashCode
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Данные пользователя для администатора")
public class InfoUsersDto {

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

    @Schema(description = "Время последнего входа", example = "2022-11-03T23:15:09.61")
    private final String loginDate;

    public static InfoUsersDto fromUser(final User user, final int zone) {
        return new InfoUsersDto(user.getUserId(), user.getName(), user.getLogin(), user.getEmail(),
                user.getCity(), user.isAccountNonLocked(), user.isEnabled(),
                user.getLoginDate() == 0 ? "0" :
                LocalDateTime.ofEpochSecond(user.getLoginDate(), 0, ZoneOffset.ofHours(zone))
                        .toString());
    }

    public static InfoUsersDto fromUserDto(final UserDto user, final int zone) {
        return new InfoUsersDto(user.getUserId(), user.getName(), user.getLogin(), user.getEmail(),
                user.getCity(), user.isAccountNonLocked(), user.isEnabled(),
                user.getLoginDate() == 0 ? "0" :
                        LocalDateTime.ofEpochSecond(user.getLoginDate(), 0, ZoneOffset.ofHours(zone))
                                .toString());
    }
}
