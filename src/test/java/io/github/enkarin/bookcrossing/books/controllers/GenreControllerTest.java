package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.GenreDto;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenreControllerTest extends BookCrossingBaseTests {

    @Test
    void findAllGenre() {
        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder.pathSegment("genre").queryParam("locale", "ru").build())
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(GenreDto.class)
            .returnResult().getResponseBody();
        assertThat(response).hasSize(101).extracting(GenreDto::getName).contains("Роман", "Повесть", "Рассказ", "Статья");
    }

    @Test
    void findAllGenreWithUnexpectedLocale() {
        webClient.get()
            .uri(uriBuilder -> uriBuilder.pathSegment("genre").queryParam("locale", "kz").build())
            .exchange()
            .expectStatus().isEqualTo(400)
            .expectBody().jsonPath("$.locale").isEqualTo("Локаль должна быть 'ru' или 'eng'");
    }
}
