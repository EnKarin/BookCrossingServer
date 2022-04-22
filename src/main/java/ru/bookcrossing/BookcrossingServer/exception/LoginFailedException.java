package ru.bookcrossing.BookcrossingServer.exception;

public class LoginFailedException extends RuntimeException{
    public LoginFailedException(){
        super("login: Пользователь с таким логином уже существует");
    }
}
