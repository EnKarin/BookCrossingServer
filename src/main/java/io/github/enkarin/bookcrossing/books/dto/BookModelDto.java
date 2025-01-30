package io.github.enkarin.bookcrossing.books.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;

@Immutable
@Getter
@SuperBuilder
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Полные данные книги")
public class BookModelDto {

    @Delegate
    @JsonIgnore
    private final BookDto bookDto;

    @Schema(description = "Идентификатор", example = "15")
    private final int bookId;

    @Schema(description = "Вложение")
    private final AttachmentDto attachment;

    public static BookModelDto fromBook(final Book book) {
        return new BookModelDto(BookDto.create(book.getTitle(), book.getAuthor(), book.getGenre(),
                book.getPublishingHouse(), book.getYear()),
                book.getBookId(),
                AttachmentDto.fromAttachment(book.getAttachment()));
    }
}
