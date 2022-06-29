package io.github.enkarin.bookcrossing.exception;

public class ChatAlreadyCreatedException extends RuntimeException {

    private static final long serialVersionUID = 7621674636156119019L;

    public ChatAlreadyCreatedException() {
        super("Чат с пользователем уже существует");
    }
}
