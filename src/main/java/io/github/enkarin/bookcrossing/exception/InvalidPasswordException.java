package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class InvalidPasswordException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1026451562048965559L;

    public InvalidPasswordException() {
        super("Некорректный пароль");
    }
}
