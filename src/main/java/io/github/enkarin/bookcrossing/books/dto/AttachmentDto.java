package io.github.enkarin.bookcrossing.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Schema(description = "Сущность вложения книги")
public class AttachmentDto {

    @Schema(description = "Идентификатор книги", example = "5")
    private final int bookId;

    @Schema(description = "Вложение")
    private final MultipartFile file;

    public AttachmentDto(int bookId, MultipartFile file){
        this.bookId = bookId;
        this.file = file;
    }
}
