package io.github.enkarin.bookcrossing.mail.enums;

import lombok.Getter;

@Getter
public enum ApproveType {
    MAIL("Подтверждение почты"),
    RESET("Сброс пароля");

    private final String localizedValue;

    ApproveType(final String local) {
        localizedValue = local;
    }
}
