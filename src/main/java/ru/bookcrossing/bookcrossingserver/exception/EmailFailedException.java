package ru.bookcrossing.bookcrossingserver.exception;

public class EmailFailedException extends RuntimeException{
    public EmailFailedException(){
        super("email: Пользователь с таким почтовым адресом уже существует");
    }
}
