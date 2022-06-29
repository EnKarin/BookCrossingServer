package io.github.enkarin.bookcrossing.exception;

public class NoAccessToChatException extends RuntimeException {

    private static final long serialVersionUID = -3053584865168748160L;

    public NoAccessToChatException() {
        super("Нет доступа к чату");
    }
}
