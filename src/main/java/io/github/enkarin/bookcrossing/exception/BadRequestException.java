package io.github.enkarin.bookcrossing.exception;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = -3438562802209326954L;

    public BadRequestException(final String message) {
        super(message);
    }
}
