package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class TokenInvalidException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3136322087373024557L;

    public TokenInvalidException() {
        super("Токен недействителен");
    }
}
