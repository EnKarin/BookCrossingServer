package ru.bookcrossing.BookcrossingServer.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.bookcrossing.BookcrossingServer.books.model.Attachment;
import ru.bookcrossing.BookcrossingServer.books.model.Book;

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

    public BookResponse(Book book){
        bookId = book.getId();
        title = book.getTitle();
        author = book.getAuthor();
        genre = book.getGenre();
        publishingHouse = book.getPublishingHouse();
        year = book.getYear();
        attachment = book.getAttachment();
    }
}
