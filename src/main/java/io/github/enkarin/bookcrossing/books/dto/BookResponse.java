package io.github.enkarin.bookcrossing.books.dto;

import io.github.enkarin.bookcrossing.books.model.Attachment;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Данные книги для общего доступа")
@Data
public class BookResponse {

    @Schema(description = "Идентификатор", example = "15")
    private int bookId;

    @Schema(description = "Название", example = "Портрет Дориана Грея")
    private String title;

    @Schema(description = "Автор", example = "Оскар Уайльд")
    private String author;

    @Schema(description = "Жанр", example = "Классическая проза")
    private String genre;

    @Schema(description = "Издательство", example = "АСТ")
    private String publishingHouse;

    @Schema(description = "Год издания", example = "2004")
    private int year;

    @Schema(description = "Вложение")
    private Attachment attachment;

    public BookResponse(final Book book) {
        bookId = book.getBookId();
        title = book.getTitle();
        author = book.getAuthor();
        genre = book.getGenre();
        publishingHouse = book.getPublishingHouse();
        year = book.getYear();
        attachment = book.getAttachment();
    }
}
