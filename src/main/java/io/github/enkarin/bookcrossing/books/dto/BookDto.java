package io.github.enkarin.bookcrossing.books.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotBlank;

@ToString
@Immutable
@Getter
@SuperBuilder
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "Сущность книги")
public class BookDto {

    @Schema(description = "Название", example = "Портрет Дориана Грея")
    @NotBlank(message = "3005")
    protected final String title;

    @Schema(description = "Автор", example = "Оскар Уайльд")
    @NotBlank(message = "3006")
    protected final String author;

    @Schema(description = "Идентификатор жанра", example = "12")
    protected final int genre;

    @Schema(description = "Издательство", example = "АСТ")
    protected final String publishingHouse;

    @Schema(description = "Год издания", example = "2004")
    protected final int year;

    @Schema(description = "id статуса", example = "1")
    protected final int statusId;

    @JsonCreator
    public static BookDto create(final String title, final String author, final int genre, final String publishingHouse, final int year, final int statusId) {
        return new BookDto(title, author, genre, publishingHouse, year, statusId);
    }
}
