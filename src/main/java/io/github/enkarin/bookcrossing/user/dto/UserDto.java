package io.github.enkarin.bookcrossing.user.dto;

import io.github.enkarin.bookcrossing.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.concurrent.Immutable;

@Immutable
@EqualsAndHashCode(callSuper = true)
@Getter
@Schema(description = "Данные пользователя")
public class UserDto extends UserParentDto {

    @Schema(description = "Время последнего входа", example = "19845673")
    private final long loginDate;

    private UserDto(final int userId, final String name, final String login, final String email, final String city,
                    final boolean accountNonLocked, final boolean enabled, final long loginDate) {
        super(userId, name, login, email, city, accountNonLocked, enabled);
        this.loginDate = loginDate;

    }

    public static UserDto fromUser(final User user) {
        return new UserDto(user.getUserId(), user.getName(), user.getLogin(), user.getEmail(),
                user.getCity(), user.isAccountNonLocked(), user.isEnabled(), user.getLoginDate());
    }
}
