package ru.bookcrossing.BookcrossingServer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.bookcrossing.BookcrossingServer.exception.InvalidTokenException;

@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Токен некорректен или устарел")
    @ExceptionHandler(InvalidTokenException.class)
    public void incorrectToken() {
    }
}
