package ru.bookcrossing.BookcrossingServer.exception;

public class MessageNotFountException extends RuntimeException{
    public MessageNotFountException(){
        super("Сообщения не существует");
    }
}
