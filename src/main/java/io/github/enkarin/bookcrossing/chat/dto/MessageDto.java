package io.github.enkarin.bookcrossing.chat.dto;

import io.github.enkarin.bookcrossing.chat.model.Message;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.annotation.concurrent.Immutable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@SuperBuilder
@Jacksonized
@EqualsAndHashCode
@Immutable
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageDto {

    private final long messageId;

    private final int sender;

    private final String text;

    private final String departureDate;

    private final boolean declaim;

    public static MessageDto fromMessageAndZone(final Message message, final int zone) {
        return new MessageDto(message.getMessageId(), message.getSender().getUserId(), message.getText(),
                LocalDateTime.ofEpochSecond(message.getDepartureDate(),
                        0, ZoneOffset.ofHours(zone)).toString(), message.isDeclaim());
    }

    public static MessageDto fromMessage(final Message message) {
        return new MessageDto(message.getMessageId(), message.getSender().getUserId(), message.getText(),
                LocalDateTime.ofEpochSecond(message.getDepartureDate(),
                        0, ZoneOffset.ofHours(0)).toString(), message.isDeclaim());
    }
}
