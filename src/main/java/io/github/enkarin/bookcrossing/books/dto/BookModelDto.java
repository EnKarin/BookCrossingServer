package io.github.enkarin.bookcrossing.books.dto;

import io.github.enkarin.bookcrossing.books.model.Attachment;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@SuperBuilder
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Полные данные книги")
public class BookModelDto {

    @Schema(description = "Идентификатор", example = "15")
    int bookId;

    @Schema(description = "Название", example = "Портрет Дориана Грея")
    @NotBlank(message = "title: Название должно содержать хотя бы один видимый символ")
    String title;

    @Schema(description = "Автор", example = "Оскар Уайльд")
    @NotBlank(message = "author: Поле \"автор\" должно содержать хотя бы один видимый символ")
    String author;

    @Schema(description = "Жанр", example = "Классическая проза")
    String genre;

    @Schema(description = "Издательство", example = "АСТ")
    String publishingHouse;

    @Schema(description = "Год издания", example = "2004")
    int year;

    @Schema(description = "Вложение")
    Attachment attachment;

    public static BookModelDto fromBook(final Book book) {
        return new BookModelDto(book.getBookId(), book.getTitle(), book.getAuthor(), book.getGenre(),
                book.getPublishingHouse(), book.getYear(), book.getAttachment());
    }
}
