package io.github.enkarin.bookcrossing.chat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotBlank;

@Immutable
@SuperBuilder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessagePutRequest {

    @Schema(description = "Идентификатор сообщения")
    private final long messageId;

    @Schema(description = "Текст сообщения")
    @NotBlank(message = "message: Сообщение должно состоять хотя бы из одного видимого символа")
    private final String text;

    @JsonCreator
    public static MessagePutRequest create(final long messageId, final String text) {
        return new MessagePutRequest(messageId, text);
    }
}
