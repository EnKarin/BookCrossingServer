package io.github.enkarin.bookcrossing.books.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.enkarin.bookcrossing.books.model.Attachment;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;
import java.util.List;

@ToString(callSuper = true)
@Immutable
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Полные данные книги")
public class BookModelDto extends BookDto {
    @Schema(description = "Идентификатор", example = "15")
    private final int bookId;

    @Schema(description = "Идентификатор титульного вложения")
    private final Integer titleAttachmentId;

    @Schema(description = "Идентификаторы дополнительных вложений")
    private final List<Integer> additionalAttachmentIdList;

    @Schema(description = "Город, в котором сейчас находится книга", example = "Новосибирск")
    protected final String city;

    private BookModelDto(final BookDto bookDto, final int bookId, final Integer titleAttachment, final List<Integer> additionalAttachmentIdList, final String city) {
        super(bookDto.title, bookDto.author, bookDto.genre, bookDto.publishingHouse, bookDto.year);
        this.bookId = bookId;
        this.city = city;
        this.titleAttachmentId = titleAttachment;
        this.additionalAttachmentIdList = additionalAttachmentIdList;
    }

    @JsonCreator
    private BookModelDto(final String title,
                         final String author,
                         final int genre,
                         final String publishingHouse,
                         final int year,
                         final int bookId,
                         final Integer attachment,
                         final List<Integer> additionalAttachmentIdList,
                         final String city) {
        super(title, author, genre, publishingHouse, year);
        this.bookId = bookId;
        this.city = city;
        this.titleAttachmentId = attachment;
        this.additionalAttachmentIdList = additionalAttachmentIdList;
    }

    public static BookModelDto fromBook(final Book book) {
        final int titleAttachmentId = book.getTitleAttachment().getAttachId();
        return new BookModelDto(create(book.getTitle(),
            book.getAuthor(),
            book.getGenre().getId(),
            book.getPublishingHouse(),
            book.getYear()),
            book.getBookId(),
            titleAttachmentId,
            book.getAttachments().stream().map(Attachment::getAttachId).filter(id -> !id.equals(titleAttachmentId)).toList(),
            book.getOwner().getCity());
    }
}
