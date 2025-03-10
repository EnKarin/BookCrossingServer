package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class UnsupportedImageTypeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3438562802209326954L;

    public UnsupportedImageTypeException(final String message) {
        super(message);
    }

    public UnsupportedImageTypeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
