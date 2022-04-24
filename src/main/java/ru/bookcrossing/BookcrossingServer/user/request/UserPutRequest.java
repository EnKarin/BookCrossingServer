package ru.bookcrossing.BookcrossingServer.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Data
@Schema(description = "Сущность для изменения данных пользователя")
public class UserPutRequest {
    @Schema(description = "Имя", example = "Alex")
    @NotBlank(message = "Имя должно содержать хотя бы один видимый символ")
    private String name;

    @Schema(description = "Старый пароль", example = "123456")
    @NotBlank(message = "Пароль должен содержать хотя бы один видимый символ")
    private String password;

    @Schema(description = "Город", example = "Новосибирск")
    private String city;
}
