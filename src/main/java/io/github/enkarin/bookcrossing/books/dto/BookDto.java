package io.github.enkarin.bookcrossing.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Getter
@Schema(description = "Сущность книги")
public class BookDto {

    @Schema(description = "Идентификатор", example = "15")
    private final int bookId;

    @Schema(description = "Название", example = "Портрет Дориана Грея")
    @NotBlank(message = "title: Название должно содержать хотя бы один видимый символ")
    private final String title;

    @Schema(description = "Автор", example = "Оскар Уайльд")
    @NotBlank(message = "author: Поле \"автор\" должно содержать хотя бы один видимый символ")
    private final String author;

    @Schema(description = "Жанр", example = "Классическая проза")
    private final String genre;

    @Schema(description = "Издательство", example = "АСТ")
    private final String publishingHouse;

    @Schema(description = "Год издания", example = "2004")
    private final int year;

    public BookDto(int bookId, String title, String author, String genre, String publishingHouse, int year) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publishingHouse = publishingHouse;
        this.year = year;
    }
}
