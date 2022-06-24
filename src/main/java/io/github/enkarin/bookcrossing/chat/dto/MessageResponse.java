package io.github.enkarin.bookcrossing.chat.dto;

import io.github.enkarin.bookcrossing.chat.model.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
public class MessageResponse {

    private long messageId;

    private int sender;

    private String text;

    private String departureDate;

    private boolean declaim;

    public MessageResponse(final Message message, final int zone) {
        messageId = message.getMessageId();
        sender = message.getSender().getUserId();
        text = message.getText();
        departureDate = LocalDateTime.ofEpochSecond(message.getDepartureDate(),
                0, ZoneOffset.ofHours(zone)).toString();
        declaim = message.isDeclaim();
    }
}
