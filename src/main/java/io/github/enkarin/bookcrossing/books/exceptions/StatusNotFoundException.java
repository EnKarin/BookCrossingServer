package io.github.enkarin.bookcrossing.books.exceptions;

import io.github.enkarin.bookcrossing.constant.ErrorMessage;

public class StatusNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StatusNotFoundException() {
        super(ErrorMessage.ERROR_1017.getCode());
    }
}
