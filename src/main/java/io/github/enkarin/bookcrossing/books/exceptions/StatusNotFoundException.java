package io.github.enkarin.bookcrossing.books.exceptions;

public class StatusNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StatusNotFoundException() {
        super("Статус не был найден");
    }
}
