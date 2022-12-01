package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class UserNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6343774856166509972L;

    public UserNotFoundException() {
        super("Пользователь не найден");
    }
}
