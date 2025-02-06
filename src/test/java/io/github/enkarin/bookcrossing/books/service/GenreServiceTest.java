package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.GenreDto;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class GenreServiceTest extends BookCrossingBaseTests {
    @Autowired
    private GenreService genreService;

    @Test
    void findAllGenre() {
        assertThat(genreService.findAllGenre()).hasSize(101)
            .contains(new GenreDto(1, "Роман", "Novel"), new GenreDto(3, "Рассказ", "Story"));
    }
}