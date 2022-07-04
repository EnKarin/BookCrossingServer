package io.github.enkarin.bookcrossing.exception;

public class PasswordsDontMatchException extends RuntimeException {

    private static final long serialVersionUID = -7199281846191977601L;

    public PasswordsDontMatchException() {
        super("Пароли не совпадают");
    }
}
