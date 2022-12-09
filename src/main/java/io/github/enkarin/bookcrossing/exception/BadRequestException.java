package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class BadRequestException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3438562802209326954L;

    public BadRequestException(final String message) {
        super(message);
    }
}
