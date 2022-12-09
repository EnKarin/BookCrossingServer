package io.github.enkarin.bookcrossing.exception;

import java.io.Serial;

public class TokenNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5790135591497624700L;

    public TokenNotFoundException() {
        super("Токен не найден");
    }
}
