package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class EmailFailedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3525532284949593144L;

    public EmailFailedException() {
        super("Пользователь с таким почтовым адресом уже существует");
    }
}
