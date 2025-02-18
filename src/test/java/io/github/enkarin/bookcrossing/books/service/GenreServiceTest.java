package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.GenreDto;
import io.github.enkarin.bookcrossing.exception.LocaleNotFoundException;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenreServiceTest extends BookCrossingBaseTests {
    @Autowired
    private GenreService genreService;

    @Test
    void findAllRuGenre() {
        assertThat(genreService.findAllGenre("ru")).hasSize(101)
            .contains(new GenreDto(1, "Роман"), new GenreDto(3, "Рассказ"));
    }

    @Test
    void findAllEngGenre() {
        assertThat(genreService.findAllGenre("en")).hasSize(101)
            .contains(new GenreDto(1, "Novel"), new GenreDto(3, "Story"));
    }

    @Test
    void findAllGenreWithUnexpectedLocaleMustThrowException() {
        assertThatThrownBy(() -> genreService.findAllGenre("fr")).isInstanceOf(LocaleNotFoundException.class);
    }
}
