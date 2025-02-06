package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class LocaleNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 0;

    public LocaleNotFoundException() {
        super("Локаль должна быть 'ru' или 'eng'");
    }
}
