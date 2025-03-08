package io.github.enkarin.bookcrossing.user.dto;

import io.github.enkarin.bookcrossing.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString
@Getter
@EqualsAndHashCode
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Данные пользователя для внутреннего пользования")
public class UserProfileDto {

    @Schema(description = "Идентификатор", example = "0")
    private final int userId;

    @Schema(description = "Имя", example = "Alex")
    private final String name;

    @Schema(description = "Логин", example = "Alex")
    private final String login;

    @Schema(description = "Город", example = "Новосибирск")
    private final String city;

    @Schema(description = "Почтовый адрес", example = "kar@mail.ru")
    private final String email;

    @Schema(description = "Информация о пользователе")
    protected final String aboutMe;

    public static UserProfileDto fromUser(final User user) {
        return new UserProfileDto(user.getUserId(), user.getName(), user.getLogin(), user.getCity(), user.getEmail(), user.getAboutMe());
    }
}
