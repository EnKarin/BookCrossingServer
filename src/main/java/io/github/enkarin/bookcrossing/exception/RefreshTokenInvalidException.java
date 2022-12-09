package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class RefreshTokenInvalidException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 586552323123678540L;

    public RefreshTokenInvalidException() {
        super("Токен истек");
    }
}
