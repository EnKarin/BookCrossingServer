package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class MessageNotFountException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6412869639668060005L;

    public MessageNotFountException() {
        super("Сообщения не существует");
    }
}
