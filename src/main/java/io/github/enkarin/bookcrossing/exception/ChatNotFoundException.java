package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class ChatNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7388406758544792815L;

    public ChatNotFoundException() {
        super("Чата не существует");
    }
}
