package io.github.enkarin.bookcrossing.books.exceptions;

public class StatusNotFoundException extends RuntimeException {
    public StatusNotFoundException() {
        super("Статус не был найден");
    }
}
