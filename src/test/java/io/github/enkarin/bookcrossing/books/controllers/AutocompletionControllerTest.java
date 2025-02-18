package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AutocompletionControllerTest extends BookCrossingBaseTests {

    @Test
    void bookNameAutocompletion() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildBot());
        bookService.saveBook(TestDataProvider.buildDandelion(), user.getLogin());
        bookService.saveBook(TestDataProvider.buildWolves(), user.getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin());

        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("books", "autocompletion", "title")
                .queryParam("name", "i")
                .build())
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(String[].class)
            .returnResult().getResponseBody();
        assertThat(response).containsOnly("Dandelion", "Dorian");
    }

    @Test
    void bookAuthorAutocompletion() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildBot());
        bookService.saveBook(TestDataProvider.buildDandelion(), user.getLogin());
        bookService.saveBook(TestDataProvider.buildWolves(), user.getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin());

        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("books", "autocompletion", "author")
                .queryParam("name", "2")
                .build())
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(String[].class)
            .returnResult().getResponseBody();
        assertThat(response).containsOnly("author2");
    }
}
