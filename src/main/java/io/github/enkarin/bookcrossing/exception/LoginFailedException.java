package io.github.enkarin.bookcrossing.exception;

public class LoginFailedException extends RuntimeException{
    public LoginFailedException(){
        super("login: Пользователь с таким логином уже существует");
    }
}
