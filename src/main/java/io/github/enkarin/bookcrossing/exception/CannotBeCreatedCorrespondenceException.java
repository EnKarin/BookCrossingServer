package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class CannotBeCreatedCorrespondenceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7584442602716343880L;

    public CannotBeCreatedCorrespondenceException() {
        super("С выбранным пользователем нельзя создать чат");
    }
}
