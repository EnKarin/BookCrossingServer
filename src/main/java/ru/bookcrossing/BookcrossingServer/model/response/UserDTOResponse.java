package ru.bookcrossing.BookcrossingServer.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bookcrossing.BookcrossingServer.entity.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Schema(description = "Сущность пользователя")
@Data
public class UserDTOResponse {
    @Schema(description = "Имя", example = "Alex")
    @NotBlank(message = "Имя должно содержать хотя бы один видимый символ")
    private String name;

    @Schema(description = "Логин", example = "LogAll")
    @NotBlank(message = "Логин должен содержать хотя бы один видимый символ")
    private String login;

    @Schema(description = "Почта", example = "al@yandex.ru")
    @Email(message = "Некорректный почтовый адрес")
    private String email;

    @Schema(description = "Город", example = "Новосибирск")
    private String city;

    public UserDTOResponse(User user){
        name = user.getName();
        login = user.getLogin();
        email = user.getEmail();
        city = user.getCity();
    }
}
