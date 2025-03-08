package io.github.enkarin.bookcrossing.admin.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import io.github.enkarin.bookcrossing.user.dto.UserParentDto;
import io.github.enkarin.bookcrossing.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.concurrent.Immutable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Immutable
@Getter
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Данные пользователя для администатора")
public class InfoUsersDto extends UserParentDto {
    @Schema(description = "Время последнего входа", example = "2022-11-03T23:15:09.61")
    private final String loginDate;

    private InfoUsersDto(final UserParentDto userParentDto, final String loginDate) {
        super(userParentDto.getUserId(),
            userParentDto.getName(),
            userParentDto.getLogin(),
            userParentDto.getEmail(),
            userParentDto.getCity(),
            userParentDto.isAccountNonLocked(),
            userParentDto.isEnabled(),
            userParentDto.getAboutMe());
        this.loginDate = loginDate;
    }

    @JsonCreator
    private InfoUsersDto(final int userId,
                         final String name,
                         final String login,
                         final String email,
                         final String city,
                         final boolean accountNonLocked,
                         final boolean enabled,
                         final String loginDate,
                         final String aboutMe) {
        super(userId, name, login, email, city, accountNonLocked, enabled, aboutMe);
        this.loginDate = loginDate;
    }

    public static InfoUsersDto fromUser(final User user, final int zone) {
        return new InfoUsersDto(create(user.getUserId(), user.getName(), user.getLogin(), user.getEmail(),
            user.getCity(), user.isAccountNonLocked(), user.isEnabled(), user.getAboutMe()),
            loginDateToString(user.getLoginDate(), zone));
    }

    public static InfoUsersDto fromUserDto(final UserDto user, final int zone) {
        return new InfoUsersDto(create(user.getUserId(), user.getName(), user.getLogin(), user.getEmail(),
            user.getCity(), user.isAccountNonLocked(), user.isEnabled(), user.getAboutMe()),
            loginDateToString(user.getLoginDate(), zone));
    }

    private static String loginDateToString(final long loginDate, final int zone) {
        return loginDate == 0 ? "0" : LocalDateTime.ofEpochSecond(loginDate, 0, ZoneOffset.ofHours(zone)).toString();
    }
}
