package io.github.enkarin.bookcrossing.exception;

public class RefreshTokenInvalidException extends RuntimeException {

    private static final long serialVersionUID = 586552323123678540L;

    public RefreshTokenInvalidException() {
        super("Токен истек");
    }
}
