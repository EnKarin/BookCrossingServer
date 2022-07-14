package io.github.enkarin.bookcrossing.exception;

public class LoginFailedException extends RuntimeException {

    private static final long serialVersionUID = 2924299242293022953L;

    public LoginFailedException() {
        super("Пользователь с таким логином уже существует");
    }
}
