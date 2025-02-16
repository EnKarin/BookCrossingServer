package io.github.enkarin.bookcrossing.books.exceptions;

import java.io.Serial;

public class NoAccessToAttachmentException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 0;

    public NoAccessToAttachmentException() {
        super("Вложение не принадлежит этому пользователю");
    }
}
