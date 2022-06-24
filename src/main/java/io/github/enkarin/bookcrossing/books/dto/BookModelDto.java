package io.github.enkarin.bookcrossing.books.dto;

import io.github.enkarin.bookcrossing.books.model.Attachment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "Полные данные книги")
@Data
public class BookModelDto {
    @Schema(description = "Идентификатор", example = "15")
    private int bookId;

    @Schema(description = "Название", example = "Портрет Дориана Грея")
    @NotBlank(message = "title: Название должно содержать хотя бы один видимый символ")
    private String title;

    @Schema(description = "Автор", example = "Оскар Уайльд")
    @NotBlank(message = "author: Поле \"автор\" должно содержать хотя бы один видимый символ")
    private String author;

    @Schema(description = "Жанр", example = "Классическая проза")
    private String genre;

    @Schema(description = "Издательство", example = "АСТ")
    private String publishingHouse;

    @Schema(description = "Год издания", example = "2004")
    private int year;

    @Schema(description = "Вложение")
    private Attachment attachment;
}
