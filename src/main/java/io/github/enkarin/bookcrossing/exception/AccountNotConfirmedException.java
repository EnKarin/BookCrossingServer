package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class AccountNotConfirmedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4549420038504853312L;

    public AccountNotConfirmedException() {
        super("Аккаунт не подтвержден");
    }
}
