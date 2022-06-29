package io.github.enkarin.bookcrossing.exception;

public class AttachmentNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -8576405155299342296L;

    public AttachmentNotFoundException() {
        super("Вложение не найдено");
    }
}
