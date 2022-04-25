package ru.bookcrossing.BookcrossingServer.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(){
        super("Пользователь не найден");
    }
}
