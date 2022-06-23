package io.github.enkarin.bookcrossing.chat.dto;

import io.github.enkarin.bookcrossing.chat.model.Message;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class MessageResponse {

    private long messageId;

    private int userId;

    private String text;

    private String date;

    private boolean declaim;

    public MessageResponse(final Message message, final int zone) {
        messageId = message.getMessageId();
        userId = message.getSender().getUserId();
        text = message.getText();
        date = LocalDateTime.ofEpochSecond(message.getDepartureDate(), 0, ZoneOffset.ofHours(zone)).toString();
        declaim = message.isDeclaim();
    }
}
