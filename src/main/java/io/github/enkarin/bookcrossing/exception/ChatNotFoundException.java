package io.github.enkarin.bookcrossing.exception;

public class ChatNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 7388406758544792815L;

    public ChatNotFoundException() {
        super("Чата не существует");
    }
}
