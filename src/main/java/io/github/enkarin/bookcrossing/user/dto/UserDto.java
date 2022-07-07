package io.github.enkarin.bookcrossing.user.dto;

import io.github.enkarin.bookcrossing.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
@Schema(description = "Данные пользователя")
public class UserDto {
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
    private final long loginDate;

    private UserDto(final int userId, final String name, final String login, final String email, final String city,
                         final boolean accountNonLocked, final boolean enable, final long loginDate) {
        this.userId = userId;
        this.name = name;
        this.login = login;
        this.email = email;
        this.city = city;
        this.accountNonLocked = accountNonLocked;
        this.enabled = enable;
        this.loginDate = loginDate;
    }

    public static UserDto fromUser(final User user) {
        return new UserDto(user.getUserId(), user.getName(), user.getLogin(), user.getEmail(),
                user.getCity(), user.isAccountNonLocked(), user.isEnabled(), user.getLoginDate());
    }
}
