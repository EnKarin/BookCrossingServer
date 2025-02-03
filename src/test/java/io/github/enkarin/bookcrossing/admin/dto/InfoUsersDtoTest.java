package io.github.enkarin.bookcrossing.admin.dto;

import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InfoUsersDtoTest extends BookCrossingBaseTests {

    @Test
    void fromUserDtoShouldWorkWithLoginDate() {
        assertThat(InfoUsersDto.fromUserDto(TestDataProvider.buildUserDto(), 0))
            .isNotNull()
            .satisfies(i -> {
                assertThat(i.getLoginDate())
                    .isEqualTo("1970-01-12T13:46:39");
                assertThat(i.getLogin())
                    .isEqualTo("login");
                assertThat(i.getName())
                    .isEqualTo("UserName");
            });
    }
}
