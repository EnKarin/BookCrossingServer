package io.github.enkarin.bookcrossing.exception;

public class TokenNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -5790135591497624700L;

    public TokenNotFoundException() {
        super("Токен не найден");
    }
}
