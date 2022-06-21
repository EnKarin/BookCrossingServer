package ru.bookcrossing.bookcrossingserver.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(){
        super("Пользователь не найден");
    }
}
