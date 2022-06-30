package io.github.enkarin.bookcrossing.exception;

public class TokenInvalidException extends RuntimeException {

    private static final long serialVersionUID = 3136322087373024557L;

    public TokenInvalidException() {
        super("Токен недействителен");
    }
}
