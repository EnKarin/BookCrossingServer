package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class PasswordsDontMatchException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7199281846191977601L;

    public PasswordsDontMatchException() {
        super("Пароли не совпадают");
    }
}
