package io.github.enkarin.bookcrossing.user.dto;

import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.user.model.User;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileDtoTest extends BookCrossingBaseTests {

    @Test
    void fromUserTest() {
        final User user = new User();
        user.setUserId(Integer.MAX_VALUE);
        user.setLogin("test");
        user.setBooks(Stream.of(new Book()).collect(Collectors.toSet()));

        final UserProfileDto userProfileDto = UserProfileDto.fromUser(user);
        assertThat(userProfileDto)
                .isNotNull()
                .extracting(UserProfileDto::getLogin)
                .isEqualTo("test");
        assertThat(userProfileDto.getUserId())
                .isEqualTo(Integer.MAX_VALUE);
        assertThat(userProfileDto.getBooks())
                .hasSize(1)
                .isUnmodifiable();
    }
}
