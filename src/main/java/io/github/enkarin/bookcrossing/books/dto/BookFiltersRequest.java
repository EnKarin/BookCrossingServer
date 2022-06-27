package io.github.enkarin.bookcrossing.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Фильтры для книг")
@Getter
public class BookFiltersRequest {

    @Schema(description = "Город", example = "Новосибирск")
    private final String city;

    @Schema(description = "Название", example = "Портрет Дориана Грея")
    private final String title;

    @Schema(description = "Автор", example = "Оскар Уайльд")
    private final String author;

    @Schema(description = "Жанр", example = "Классическая проза")
    private final String genre;

    @Schema(description = "Издательство", example = "АСТ")
    private final String publishingHouse;

    @Schema(description = "Год издания", example = "2004")
    private final int year;

    public BookFiltersRequest(String city, String title, String genre, String author, String publishingHouse,
                              int year) {
        this.city = city;
        this.title = title;
        this.genre = genre;
        this.author = author;
        this.publishingHouse = publishingHouse;
        this.year = year;
    }
}
