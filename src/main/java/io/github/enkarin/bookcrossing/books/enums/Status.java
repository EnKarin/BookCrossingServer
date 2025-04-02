package io.github.enkarin.bookcrossing.books.enums;

import io.github.enkarin.bookcrossing.books.exceptions.StatusNotFoundException;
import lombok.Getter;

@Getter
public enum Status {
    GIVE(1, "Отдает", "Gives away"),
    EXCHANGES(2, "Отдает", "Exchanges");

    private final Integer id;
    private final String ru;
    private final String en;

    Status(final int id, final String ru, final String en) {
        this.id = id;
        this.ru = ru;
        this.en = en;
    }

    public static Status getById(int id) {
        for (Status status : Status.values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new StatusNotFoundException();
    }
}
