package ru.bookcrossing.BookcrossingServer.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Data
@Schema(description = "Сущность для сообщения")
public class MessageRequest {

    @Schema(description = "Идентификатор чата")
    private UsersCorrKeyDto usersCorrKeyDto;

    @Schema(description = "Текст сообщения")
    @NotBlank(message = "message: Сообщение должно состоять хотя бы из одного видимого символа")
    private String text;
}
