package io.github.enkarin.bookcrossing.chat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class MessagePutRequest {

    @Schema(description = "Идентификатор сообщения")
    private final long messageId;

    @Schema(description = "Текст сообщения")
    @NotBlank(message = "message: Сообщение должно состоять хотя бы из одного видимого символа")
    private final String text;

    private MessagePutRequest(final long messageId, final String text) {
        this.messageId = messageId;
        this.text = text;
    }

    @JsonCreator
    public static MessagePutRequest create(final long messageId, final String text) {
        return new MessagePutRequest(messageId, text);
    }
}
