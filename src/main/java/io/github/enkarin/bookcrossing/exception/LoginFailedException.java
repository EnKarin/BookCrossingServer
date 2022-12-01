package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class LoginFailedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2924299242293022953L;

    public LoginFailedException() {
        super("Пользователь с таким логином уже существует");
    }
}
