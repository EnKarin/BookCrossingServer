package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.GenreDto;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenreControllerTest extends BookCrossingBaseTests {

    @Test
    void findAllGenre() {
        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder.pathSegment("genre").build())
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(GenreDto.class)
            .returnResult().getResponseBody();
        assertThat(response).hasSize(101).extracting(GenreDto::getRuName).contains("Роман", "Повесть", "Рассказ", "Статья");
    }
}
