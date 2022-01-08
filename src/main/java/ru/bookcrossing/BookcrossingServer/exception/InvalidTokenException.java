package ru.bookcrossing.BookcrossingServer.exception;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String token){
        super("Некорректный токен: " + token);
    }
}
