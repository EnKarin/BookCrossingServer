package io.github.enkarin.bookcrossing.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import io.github.enkarin.bookcrossing.user.dto.UserParentDto;
import io.github.enkarin.bookcrossing.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Delegate;

import javax.annotation.concurrent.Immutable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Immutable
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Данные пользователя для администатора")
public class InfoUsersDto {

    @Delegate
    @JsonIgnore
    private final UserParentDto userParentDto;

    @Schema(description = "Время последнего входа", example = "2022-11-03T23:15:09.61")
    private final String loginDate;

    public static InfoUsersDto fromUser(final User user, final int zone) {
        return new InfoUsersDto(UserParentDto.create(user.getUserId(), user.getName(), user.getLogin(), user.getEmail(),
                user.getCity(), user.isAccountNonLocked(), user.isEnabled()),
                loginDateToString(user.getLoginDate(), zone));
    }

    public static InfoUsersDto fromUserDto(final UserDto user, final int zone) {
        return new InfoUsersDto(UserParentDto.create(user.getUserId(), user.getName(), user.getLogin(), user.getEmail(),
                user.getCity(), user.isAccountNonLocked(), user.isEnabled()),
                loginDateToString(user.getLoginDate(), zone));
    }

    private static String loginDateToString(final long loginDate, final int zone) {
        return loginDate == 0 ? "0" :
                LocalDateTime.ofEpochSecond(loginDate, 0, ZoneOffset.ofHours(zone))
                        .toString();
    }
}
