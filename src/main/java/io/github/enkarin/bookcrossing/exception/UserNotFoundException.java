package io.github.enkarin.bookcrossing.exception;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6343774856166509972L;

    public UserNotFoundException() {
        super("Пользователь не найден");
    }
}
