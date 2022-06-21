package ru.bookcrossing.bookcrossingserver.exception;

public class MessageNotFountException extends RuntimeException{
    public MessageNotFountException(){
        super("Сообщения не существует");
    }
}
