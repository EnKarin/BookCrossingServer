package ru.bookcrossing.BookcrossingServer.exception;

public class ValidateException extends IllegalArgumentException{
    private String message;

    public ValidateException(String message){

    }

    public String getMessage() {
        return message;
    }
}
