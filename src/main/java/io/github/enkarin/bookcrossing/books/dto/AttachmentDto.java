package io.github.enkarin.bookcrossing.books.dto;

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

    public static AttachmentDto fromAttachment(final Attachment attachment) {
        return attachment == null ? null : new AttachmentDto(attachment.getAttachId(), attachment.getOriginalImage(),
            attachment.getExpansion());
    }
}
