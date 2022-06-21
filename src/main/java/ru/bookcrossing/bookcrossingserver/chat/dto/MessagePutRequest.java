package ru.bookcrossing.bookcrossingserver.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MessagePutRequest {

    @Schema(description = "Идентификатор сообщения")
    private long messageId;

    @Schema(description = "Текст сообщения")
    @NotBlank(message = "message: Сообщение должно состоять хотя бы из одного видимого символа")
    private String text;
}
