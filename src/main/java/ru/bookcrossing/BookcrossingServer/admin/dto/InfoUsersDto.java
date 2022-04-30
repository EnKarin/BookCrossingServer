package ru.bookcrossing.BookcrossingServer.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.bookcrossing.BookcrossingServer.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@Schema(description = "Данные пользователя для администатора")
public class InfoUsersDto {

    @Schema(description = "Идентификатор", example = "0")
    private int userId;

    @Schema(description = "Имя", example = "Alex")
    private String name;

    @Schema(description = "Логин", example = "alex")
    private String login;

    @Schema(description = "Почта", example = "al@yandex.ru")
    private String email;

    @Schema(description = "Город", example = "Новосибирск")
    private String city;

    @Schema(description = "Заблокирован ли аккаунт", example = "true")
    private boolean accountNonLocked;

    @Schema(description = "Активирован ли аккаунт", example = "true")
    private boolean enabled;

    @Schema(description = "Время последнего входа", example = "2022-11-03T23:15:09.61")
    private String loginDate;

    public InfoUsersDto(User user, int zone){
        userId = user.getUserId();
        name = user.getName();
        login = user.getLogin();
        email = user.getEmail();
        city = user.getCity();
        accountNonLocked = user.isAccountNonLocked();
        enabled = user.isEnabled();
        loginDate = LocalDateTime.ofEpochSecond(user.getLoginDate(), 0, ZoneOffset.ofHours(zone)).toString();
    }
}
