package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class ChatAlreadyCreatedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7621674636156119019L;

    public ChatAlreadyCreatedException() {
        super("Чат с пользователем уже существует");
    }
}
