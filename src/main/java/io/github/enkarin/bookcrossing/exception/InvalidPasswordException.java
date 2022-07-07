package io.github.enkarin.bookcrossing.exception;

public class InvalidPasswordException extends RuntimeException {

    private static final long serialVersionUID = 1026451562048965559L;

    public InvalidPasswordException() {
        super("Некорректный пароль");
    }
}
