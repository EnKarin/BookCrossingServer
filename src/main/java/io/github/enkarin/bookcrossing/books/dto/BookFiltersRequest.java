package io.github.enkarin.bookcrossing.books.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.concurrent.Immutable;

@Immutable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Schema(description = "Фильтры для книг")
public class BookFiltersRequest {

    @Schema(description = "Город", example = "Новосибирск")
    private final String city;

    @Schema(description = "Название", example = "Портрет Дориана Грея")
    private final String title;

    @Schema(description = "Автор", example = "Оскар Уайльд")
    private final String author;

    @Schema(description = "Идентификатор жанра", example = "22")
    private final int genre;

    @Schema(description = "Издательство", example = "АСТ")
    private final String publishingHouse;

    @Schema(description = "Год издания", example = "2004")
    private final int year;

    @JsonCreator
    public static BookFiltersRequest create(final String city, final String title, final String author,
                                            final int genre, final String publishingHouse, final int year) {
        return new BookFiltersRequest(city, title, author, genre, publishingHouse, year);
    }
}
