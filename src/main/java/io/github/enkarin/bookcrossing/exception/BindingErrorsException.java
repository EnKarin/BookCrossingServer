package io.github.enkarin.bookcrossing.exception;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class BindingErrorsException extends RuntimeException {

    private static final long serialVersionUID = 1289402423305382529L;

    @Getter
    private final List<String> errors;

    public BindingErrorsException(final List<String> errors) {
        super("valid: Ошибка проверки входных данных");
        this.errors = errors.stream().collect(Collectors.toUnmodifiableList());
    }
}
