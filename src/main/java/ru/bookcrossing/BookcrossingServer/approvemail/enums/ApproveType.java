package ru.bookcrossing.BookcrossingServer.approvemail.enums;

import lombok.Getter;

@Getter
public enum ApproveType {
    MAIL("Подтверждение почты"),
    RESET("Сброс пароля");

    private final String localizedValue;

    ApproveType(String l){
        localizedValue = l;
    }
}
