package io.github.enkarin.bookcrossing.books.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotBlank;

@Immutable
@Getter
@SuperBuilder
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "Сущность книги")
public class BookDto {

    @Schema(description = "Название", example = "Портрет Дориана Грея")
    @NotBlank(message = "title: Название должно содержать хотя бы один видимый символ")
    protected final String title;

    @Schema(description = "Автор", example = "Оскар Уайльд")
    @NotBlank(message = "author: Поле \"автор\" должно содержать хотя бы один видимый символ")
    protected final String author;

    @Schema(description = "Жанр", example = "Классическая проза")
    protected final String genre;

    @Schema(description = "Издательство", example = "АСТ")
    protected final String publishingHouse;

    @Schema(description = "Год издания", example = "2004")
    protected final int year;

    @JsonCreator
    public static BookDto create(final String title, final String author, final String genre,
                                 final String publishingHouse, final int year) {
        return new BookDto(title, author, genre, publishingHouse, year);
    }
}
