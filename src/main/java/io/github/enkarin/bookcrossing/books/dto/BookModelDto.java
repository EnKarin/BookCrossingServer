package io.github.enkarin.bookcrossing.books.dto;

import io.github.enkarin.bookcrossing.books.model.Attachment;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@SuperBuilder
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Полные данные книги")
public class BookModelDto {

    @Getter
    @Schema(description = "Идентификатор", example = "15")
    private final int bookId;

    @Getter
    @Schema(description = "Название", example = "Портрет Дориана Грея")
    @NotBlank(message = "title: Название должно содержать хотя бы один видимый символ")
    private final String title;

    @Getter
    @Schema(description = "Автор", example = "Оскар Уайльд")
    @NotBlank(message = "author: Поле \"автор\" должно содержать хотя бы один видимый символ")
    private final String author;

    @Getter
    @Schema(description = "Жанр", example = "Классическая проза")
    private final String genre;

    @Getter
    @Schema(description = "Издательство", example = "АСТ")
    private final String publishingHouse;

    @Getter
    @Schema(description = "Год издания", example = "2004")
    private final int year;

    @Schema(description = "Вложение")
    private final Attachment attachment;

    public Attachment getAttachment() {
        if (attachment != null) {
            return new Attachment(attachment.getAttachId(), attachment.getBook(), attachment.getData(),
                    attachment.getExpansion());
        }
        return null;
    }

    public static BookModelDto fromBook(final Book book) {
        return new BookModelDto(book.getBookId(), book.getTitle(), book.getAuthor(), book.getGenre(),
                book.getPublishingHouse(), book.getYear(), book.getAttachment());
    }
}
