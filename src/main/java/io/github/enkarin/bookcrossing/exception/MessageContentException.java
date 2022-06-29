package io.github.enkarin.bookcrossing.exception;

public class MessageContentException extends RuntimeException {

    private static final long serialVersionUID = -4892619318746085962L;

    public MessageContentException() {
        super("Сообщение не может быть пустым");
    }
}
