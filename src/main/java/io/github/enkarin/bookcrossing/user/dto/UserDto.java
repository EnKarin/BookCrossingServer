package io.github.enkarin.bookcrossing.user.dto;

import io.github.enkarin.bookcrossing.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;

@Immutable
@SuperBuilder
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Данные пользователя")
public class UserDto {

    @Delegate
    private final UserParentDto userParentDto;

    @Schema(description = "Время последнего входа", example = "19845673")
    private final long loginDate;

    public static UserDto fromUser(final User user) {
        return new UserDto(UserParentDto.create(user.getUserId(), user.getName(), user.getLogin(), user.getEmail(),
                user.getCity(), user.isAccountNonLocked(), user.isEnabled()), user.getLoginDate());
    }
}
