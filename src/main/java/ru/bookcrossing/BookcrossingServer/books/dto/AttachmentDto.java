package ru.bookcrossing.BookcrossingServer.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "Сущность вложения книги")
public class AttachmentDto {

    @Schema(description = "Идентификатор книги", example = "5")
    private int bookId;

    @Schema(description = "Вложение")
    private MultipartFile file;
}
