package io.github.enkarin.bookcrossing.books.dto;

import io.github.enkarin.bookcrossing.books.enums.FormatType;
import io.github.enkarin.bookcrossing.books.model.Attachment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AttachmentDto {

    private final int attachId;

    private final byte[] data;

    private final String expansion;

    public static AttachmentDto fromAttachment(final Attachment attachment, final FormatType imageFormat) {
        return attachment == null ? null : new AttachmentDto(attachment.getAttachId(),
            switch (imageFormat) {
                case origin -> attachment.getOriginalImage();
                case list -> attachment.getListImage();
                case thumb -> attachment.getThumbImage();
            },
            imageFormat == FormatType.origin ? attachment.getOriginalImageExpansion() : "jpg");
    }
}
