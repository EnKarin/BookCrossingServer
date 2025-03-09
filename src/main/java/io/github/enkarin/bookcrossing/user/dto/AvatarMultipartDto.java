package io.github.enkarin.bookcrossing.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Сущность для добавления вложения")
public class AvatarMultipartDto {

    @Schema(description = "Идентификатор пользователя", example = "5")
    private final int userId;

    @Schema(description = "Аватар")
    private final MultipartFile avatar;

    @JsonCreator
    public static AvatarMultipartDto fromFile(final int bookId, final MultipartFile file) {
        return new AvatarMultipartDto(bookId, file);
    }
}
