package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class BookNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5536117246887103791L;

    public BookNotFoundException() {
        super("Книга не найдена");
    }
}
