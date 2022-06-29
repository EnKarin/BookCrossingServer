package io.github.enkarin.bookcrossing.exception;

public class MessageNotFountException extends RuntimeException {

    private static final long serialVersionUID = -6412869639668060005L;

    public MessageNotFountException() {
        super("Сообщения не существует");
    }
}
