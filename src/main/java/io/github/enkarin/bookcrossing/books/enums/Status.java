package io.github.enkarin.bookcrossing.books.enums;

import lombok.Getter;

@Getter
public enum Status {
    GIVE("Отдает", "Gives away"), EXCHANGES("Отдает", "Exchanges");

    private final String ru;

    private final String en;

    Status(final String ru, final String en) {
        this.ru = ru;
        this.en = en;
    }
}
