package io.github.enkarin.bookcrossing.chat.dto;

public record ChatInfo(String interlocutorName, String message, Integer lastMessageSenderId, int interlocutorId, int unreadQuantity) {
}
