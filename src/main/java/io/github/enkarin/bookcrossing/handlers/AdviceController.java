package io.github.enkarin.bookcrossing.handlers;

import io.github.enkarin.bookcrossing.constant.ErrorMessage;
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
import java.util.List;
import java.util.Map;

import static io.github.enkarin.bookcrossing.utils.Util.createErrorMap;

@RestControllerAdvice
public class AdviceController extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public Map<String, String> userNotFound() {
        return createErrorMap(ErrorMessage.ERROR_1003);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BookNotFoundException.class)
    public Map<String, String> bookNotFound() {
        return createErrorMap(ErrorMessage.ERROR_1004);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(BindingErrorsException.class)
    public Map<String, List<String>> bindingExc(final BindingErrorsException exc) {
        return Map.of("errorList", exc.getErrors());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String[]> catchParameterValidationException(final ConstraintViolationException exception) {
        return Map.of("errorList", exception.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .toArray(String[]::new));
    }
}
