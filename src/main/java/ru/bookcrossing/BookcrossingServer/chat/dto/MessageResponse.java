package ru.bookcrossing.BookcrossingServer.chat.dto;

import lombok.Data;
import ru.bookcrossing.BookcrossingServer.chat.model.Message;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class MessageResponse {

    private long messageId;

    private int userId;

    private String text;

    private String date;

    public MessageResponse(Message message, int zone){
        messageId = message.getMessageId();
        userId = message.getSender().getUserId();
        text = message.getText();
        date = LocalDateTime.ofEpochSecond(message.getDate(), 0, ZoneOffset.ofHours(zone)).toString();
    }
}
