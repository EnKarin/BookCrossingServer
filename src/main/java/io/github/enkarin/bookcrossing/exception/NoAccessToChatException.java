package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class NoAccessToChatException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3053584865168748160L;

    public NoAccessToChatException() {
        super("Нет доступа к чату");
    }
}
