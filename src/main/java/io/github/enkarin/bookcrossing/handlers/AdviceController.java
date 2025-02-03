package io.github.enkarin.bookcrossing.handlers;

import io.github.enkarin.bookcrossing.exception.BindingErrorsException;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AdviceController extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public Map<String, String> userNotFound(final UserNotFoundException exc) {
        return Map.of("user", exc.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BookNotFoundException.class)
    public Map<String, String> bookNotFound(final BookNotFoundException exc) {
        return Map.of("book", exc.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(BindingErrorsException.class)
    public Map<String, String> bindingExc(final BindingErrorsException exc) {
        return exc.getErrors().stream().collect(Collectors.toMap(s -> s.split(":")[0].trim(), s -> s.split(":")[1].trim()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> catchParameterValidationException(final ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
            .collect(Collectors.toMap(constraintViolation -> {
                final String path = constraintViolation.getPropertyPath().toString();
                return path.substring(path.lastIndexOf('.') + 1);
            }, ConstraintViolation::getMessage));
    }
}
