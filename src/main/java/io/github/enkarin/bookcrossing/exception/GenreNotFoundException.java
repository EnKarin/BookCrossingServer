package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class GenreNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 0;

    public GenreNotFoundException() {
        super("Указанный жанр не найден");
    }
}
