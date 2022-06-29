package io.github.enkarin.bookcrossing.exception;

public class BookNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -5536117246887103791L;

    public BookNotFoundException() {
        super("Книга не найдена");
    }
}
