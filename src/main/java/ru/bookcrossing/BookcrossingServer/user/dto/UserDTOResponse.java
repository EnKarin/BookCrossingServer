package ru.bookcrossing.BookcrossingServer.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.bookcrossing.BookcrossingServer.user.model.User;

@Schema(description = "Данные пользователя для общего доступа")
@Data
public class UserDTOResponse {

    @Schema(description = "Идентификатор", example = "0")
    private int userId;

    @Schema(description = "Имя", example = "Alex")
    private String name;

    @Schema(description = "Почта", example = "al@yandex.ru")
    private String email;

    @Schema(description = "Город", example = "Новосибирск")
    private String city;

    public UserDTOResponse(User user){
        userId = user.getUserId();
        name = user.getName();
        email = user.getEmail();
        city = user.getCity();
    }
}