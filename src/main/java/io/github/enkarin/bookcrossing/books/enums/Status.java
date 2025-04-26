package io.github.enkarin.bookcrossing.books.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.enkarin.bookcrossing.books.exceptions.StatusNotFoundException;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Status {
    GIVE(1, "Отдает", "Gives away"),
    EXCHANGES(2, "Отдает", "Exchanges");

    private final int id;
    private final String ru;
    private final String en;

    Status(final int id, final String ru, final String en) {
        this.id = id;
        this.ru = ru;
        this.en = en;
    }

    public static Status getById(final int id) {
        for (final Status status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new StatusNotFoundException();
    }
}
