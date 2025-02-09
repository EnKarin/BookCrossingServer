package io.github.enkarin.bookcrossing.chat.dto;

public record ChatInfo(String interlocutorName, String message, int lastMessageSenderId, int interlocutorId) {
}
