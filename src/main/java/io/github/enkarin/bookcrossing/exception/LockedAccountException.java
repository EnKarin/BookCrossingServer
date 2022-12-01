package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class LockedAccountException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -8706068297989412081L;

    public LockedAccountException() {
        super("Аккаунт заблокирован");
    }
}
