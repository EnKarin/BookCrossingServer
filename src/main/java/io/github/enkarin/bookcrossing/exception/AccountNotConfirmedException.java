package io.github.enkarin.bookcrossing.exception;

public class AccountNotConfirmedException extends RuntimeException {

    private static final long serialVersionUID = 4549420038504853312L;

    public AccountNotConfirmedException() {
        super("Аккаунт не подтвержден");
    }
}
