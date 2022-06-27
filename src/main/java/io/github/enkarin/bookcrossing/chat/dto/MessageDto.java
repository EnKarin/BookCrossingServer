package io.github.enkarin.bookcrossing.chat.dto;

import io.github.enkarin.bookcrossing.chat.model.Message;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
public class MessageDto {

    private final long messageId;

    private final int sender;

    private final String text;

    private final String departureDate;

    private final boolean declaim;

    private MessageDto(final long messageId, final int sender, final String text, final long departureDate,
                       final boolean declaim, final int zone) {
        this.messageId = messageId;
        this.sender = sender;
        this.text = text;
        this.departureDate = LocalDateTime.ofEpochSecond(departureDate,
                0, ZoneOffset.ofHours(zone)).toString();
        this.declaim = declaim;
    }

    public static MessageDto fromMessageAndZone(final Message message, final int zone) {
        return new MessageDto(message.getMessageId(), message.getSender().getUserId(), message.getText(),
                message.getDepartureDate(), message.isDeclaim(), zone);
    }

    public static MessageDto fromMessage(final Message message) {
        return new MessageDto(message.getMessageId(), message.getSender().getUserId(), message.getText(),
                message.getDepartureDate(), message.isDeclaim(), 0);
    }
}
