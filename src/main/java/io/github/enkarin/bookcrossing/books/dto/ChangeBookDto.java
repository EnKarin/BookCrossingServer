package io.github.enkarin.bookcrossing.books.dto;

import io.github.enkarin.bookcrossing.books.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сущность книги")
public class ChangeBookDto {
    @Schema(description = "Индентификатор изменяемой книги", example = "11")
    private int bookId;

    @Schema(description = "Название", example = "Портрет Дориана Грея")
    private String title;

    @Schema(description = "Автор", example = "Оскар Уайльд")
    private String author;

    @Schema(description = "Идентификатор жанра", example = "12")
    private Integer genre;

    @Schema(description = "Издательство", example = "АСТ")
    private String publishingHouse;

    @Schema(description = "Год издания", example = "2004")
    private Integer year;

    @Schema(description = "Статус", example = "Отдает")
    private Status status;
}
