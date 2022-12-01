package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class UserIsNotSenderException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 500193990892545507L;

    public UserIsNotSenderException() {
        super("Пользователь не является отправителем");
    }
}
