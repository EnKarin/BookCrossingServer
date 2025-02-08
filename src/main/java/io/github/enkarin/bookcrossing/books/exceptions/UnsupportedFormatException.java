package io.github.enkarin.bookcrossing.books.exceptions;

import java.io.Serial;

public class UnsupportedFormatException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 0;

    public UnsupportedFormatException() {
        super("Формат должен быть 'origin', 'list' или 'thumb'");
    }
}
