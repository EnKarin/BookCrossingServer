package io.github.enkarin.bookcrossing.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.enkarin.bookcrossing.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;

@Immutable
@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Данные пользователя")
public class UserDto extends UserParentDto {
    @Schema(description = "Время последнего входа", example = "19845673")
    private final long loginDate;

    private UserDto(final UserParentDto user, final long loginDate) {
        super(user.getUserId(), user.getName(), user.getLogin(), user.getEmail(), user.getCity(), user.isAccountNonLocked(), user.isEnabled(), user.getAboutMe());
        this.loginDate = loginDate;
    }

    @JsonCreator
    private UserDto(final int userId,
                    final String name,
                    final String login,
                    final String email,
                    final String city,
                    final boolean accountNonLocked,
                    final boolean enabled,
                    final long loginDate,
                    final String aboutMe) {
        super(userId, name, login, email, city, accountNonLocked, enabled, aboutMe);
        this.loginDate = loginDate;
    }

    public static UserDto fromUser(final User user) {
        return new UserDto(create(user.getUserId(), user.getName(), user.getLogin(), user.getEmail(),
            user.getCity(), user.isAccountNonLocked(), user.isEnabled(), user.getAboutMe()), user.getLoginDate());
    }
}
