package io.github.enkarin.bookcrossing.exception;

public class ChatAlreadyCreatedException extends RuntimeException {
    public ChatAlreadyCreatedException() {
        super("Чат с пользователем уже существует");
    }
}
