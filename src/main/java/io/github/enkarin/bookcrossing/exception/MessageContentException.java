package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class MessageContentException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -4892619318746085962L;

    public MessageContentException() {
        super("Сообщение не может быть пустым");
    }
}
