package ru.bookcrossing.BookcrossingServer.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Фильтры для книг")
@Data
public class BookFiltersRequest {
    @Schema(description = "Город", example = "Новосибирск")
    private String city;

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
}
