package io.github.enkarin.bookcrossing.exception;

import lombok.Getter;

import java.io.Serial;
import java.util.List;

public class BindingErrorsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1289402423305382529L;

    @Getter
    private final List<String> errors;

    public BindingErrorsException(final List<String> errors) {
        super("valid: Ошибка проверки входных данных");
        this.errors = errors.stream().toList();
    }
}
