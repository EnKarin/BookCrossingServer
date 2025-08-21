package io.github.enkarin.bookcrossing.books.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AttachmentTest {

    @Test
    void equalsAndHashCodeShouldWork() {
        final Attachment first = new Attachment();
        final Attachment second = new Attachment();

        assertThat(first)
                .isEqualTo(first)
                .isNotEqualTo(new Book())
                .isEqualTo(second)
                .hasSameHashCodeAs(second);

        first.setAttachId(11);
        assertThat(first)
                .isNotEqualTo(second)
                .hasSameHashCodeAs(second);
    }
}
