package io.github.enkarin.bookcrossing.exception;

public class LockedAccountException extends RuntimeException {

    private static final long serialVersionUID = -8706068297989412081L;

    public LockedAccountException() {
        super("Аккаунт заблокирован");
    }
}
