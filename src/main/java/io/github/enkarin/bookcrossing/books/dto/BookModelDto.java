package io.github.enkarin.bookcrossing.books.dto;

import io.github.enkarin.bookcrossing.books.model.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;

@Immutable
@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Полные данные книги")
public class BookModelDto extends BookDto {

    @Schema(description = "Идентификатор", example = "15")
    private final int bookId;

    @Schema(description = "Вложение")
    private final AttachmentDto attachment;

    private BookModelDto(final int bookId, final String title, final String author, final String genre,
                         final String publishingHouse, final int year, final AttachmentDto attachment) {
        super(title, author, genre, publishingHouse, year);
        this.bookId = bookId;
        this.attachment = attachment;
    }

    public static BookModelDto fromBook(final Book book) {
        return new BookModelDto(book.getBookId(), book.getTitle(), book.getAuthor(), book.getGenre(),
                book.getPublishingHouse(), book.getYear(), AttachmentDto.fromAttachment(book.getAttachment()));
    }
}
