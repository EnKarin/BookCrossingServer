package ru.bookcrossing.BookcrossingServer.model.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import ru.bookcrossing.BookcrossingServer.entity.Book;

import javax.validation.constraints.NotBlank;

@Validated
@Getter
@Setter
@Schema(description = "Сущность книги")
public class BookDTO {

    @Schema(description = "Название", example = "Портрет Дориана Грея")
    @NotBlank(message = "Название должно содержать хотя бы один видимый символ")
    private String title;

    @Schema(description = "Автор", example = "Оскар Уайльд")
    @NotBlank(message = "Поле \"автор\" должно содержать хотя бы один видимый символ")
    private String author;

    @Schema(description = "Жанр", example = "Классическая проза")
    private String genre;

    @Schema(description = "Издательство", example = "АСТ")
    private String publishingHouse;

    @Schema(description = "Год издания", example = "2004")
    private int year;

    public BookDTO(Book book){
        title = book.getTitle();
        author = book.getAuthor();
        genre = book.getGenre();
        publishingHouse = book.getPublishingHouse();
        year = book.getYear();
    }
}
