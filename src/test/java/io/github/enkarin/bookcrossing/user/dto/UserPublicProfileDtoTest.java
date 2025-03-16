package io.github.enkarin.bookcrossing.user.dto;

import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.user.model.User;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UserPublicProfileDtoTest {

    @Test
    void fromUserShouldWorkWithLoginDateIsZero() {
        final User user = new User();
        user.setUserId(Integer.MAX_VALUE);
        user.setName("name");
        user.setBooks(Stream.of(new Book()).collect(Collectors.toSet()));

        final UserPublicProfileDto userProfileDto = UserPublicProfileDto.fromUser(user, 3);
        assertThat(userProfileDto)
            .isNotNull()
            .satisfies(u -> {
                assertThat(Integer.parseInt(u.getUserId()))
                    .isEqualTo(Integer.MAX_VALUE);
                assertThat(u.getName())
                    .isEqualTo("name");
                assertThat(u.getLoginDate())
                    .isEqualTo("0");
            });
    }

    @Test
    void fromUserShouldWorkWithLoginDateNotZero() {
        final User user = new User();
        user.setUserId(Integer.MAX_VALUE);
        user.setName("name");
        user.setLoginDate(123_567);
        user.setBooks(Stream.of(new Book()).collect(Collectors.toSet()));

        final UserPublicProfileDto userProfileDto = UserPublicProfileDto.fromUser(user, 3);
        assertThat(userProfileDto)
            .isNotNull()
            .satisfies(u -> {
                assertThat(Integer.parseInt(u.getUserId()))
                    .isEqualTo(Integer.MAX_VALUE);
                assertThat(u.getName())
                    .isEqualTo("name");
                assertThat(u.getLoginDate())
                    .isEqualTo("1970-01-02T13:19:27");
            });
    }
}
