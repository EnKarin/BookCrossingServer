package io.github.enkarin.bookcrossing.exception;

public class MessageNotFountException extends RuntimeException {

    public MessageNotFountException() {
        super("Сообщения не существует");
    }
}
