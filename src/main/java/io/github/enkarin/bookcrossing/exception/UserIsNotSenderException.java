package io.github.enkarin.bookcrossing.exception;

public class UserIsNotSenderException extends RuntimeException {

    private static final long serialVersionUID = 500193990892545507L;

    public UserIsNotSenderException() {
        super("Неозможно отредактировать чужое сообщение");
    }
}
