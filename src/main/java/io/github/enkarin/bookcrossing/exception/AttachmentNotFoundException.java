package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class AttachmentNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -8576405155299342296L;

    public AttachmentNotFoundException() {
        super("Вложение не найдено");
    }
}
