package io.github.enkarin.bookcrossing.books.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Сущность вложения книги")
public class AttachmentDto {

    @Schema(description = "Идентификатор книги", example = "5")
    private final int bookId;

    @Schema(description = "Вложение")
    private final MultipartFile file;

    @JsonCreator
    public static AttachmentDto fromFile(final int bookId, final MultipartFile file) {
        return new AttachmentDto(bookId, file);
    }
}
