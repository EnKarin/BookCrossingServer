package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.constant.ErrorMessage;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookControllerTest extends BookCrossingBaseTests {

    @Test
    void booksShouldnWork() {
        final var user = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .findAny()
            .orElseThrow();
        enabledUser(user.getUserId());

        final var booksId = createAndSaveBooks(user.getLogin());
        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder.pathSegment("books", "all")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
                .build())
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(BookModelDto.class)
            .returnResult().getResponseBody();
        assertThat(response)
            .hasSize(3)
            .isEqualTo(TestDataProvider.buildBookModels(booksId.get(0), booksId.get(1), booksId.get(2)));
    }

    @Test
    void booksShouldnWorkWithEmptyTableBook() {
        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder.pathSegment("books", "all")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
                .build())
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(BookModelDto.class)
            .returnResult().getResponseBody();
        assertThat(response)
            .isEmpty();
    }

    @Test
    void bookInfoShouldnWork() {
        final var user = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .findAny()
            .orElseThrow();
        enabledUser(user.getUserId());
        final var bookId = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();
        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("books", "info")
                .queryParam("bookId", String.valueOf(bookId))
                .build())
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(BookModelDto.class)
            .returnResult().getResponseBody();
        assertThat(response)
            .isEqualTo(TestDataProvider.buildDorian(bookId));
    }

    @Test
    void bookInfoShouldnFailBecauseBookNotFound() {
        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("books", "info")
                .queryParam("bookId", String.valueOf(Integer.MAX_VALUE))
                .build())
            .exchange()
            .expectStatus().isEqualTo(404)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(ErrorMessage.ERROR_1004.getCode());
    }

    @Test
    void searchByTitle() {
        final var user = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .findAny()
            .orElseThrow();
        enabledUser(user.getUserId());
        final var firstBookId = bookService.saveBook(TestDataProvider.buildDandelion(), user.getLogin()).getBookId();
        final var secondBookId = bookService.saveBook(TestDataProvider.buildDandelion(), user.getLogin()).getBookId();
        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("books", "searchByTitle")
                .queryParam("name", "Dandelion")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
                .build())
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(BookModelDto.class)
            .returnResult().getResponseBody();
        assertThat(response)
            .hasSize(2)
            .isEqualTo(List.of(TestDataProvider.buildDandelion(firstBookId),
                TestDataProvider.buildDandelion(secondBookId)));
    }

    @Test
    void searchByTitleWithPagination() {
        final var user = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .findAny()
            .orElseThrow();
        enabledUser(user.getUserId());
        final var firstBookId = bookService.saveBook(TestDataProvider.buildDandelion(), user.getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDandelion(), user.getLogin());
        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("books", "searchByTitle")
                .queryParam("name", "Dandelion")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 1)
                .build())
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(BookModelDto.class)
            .returnResult().getResponseBody();
        assertThat(response)
            .hasSize(1)
            .containsOnly(TestDataProvider.buildDandelion(firstBookId));
    }

    @Test
    void searchByTitleShouldnWorkWithoutBooks() {
        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("books", "searchByTitle")
                .queryParam("name", "TestName") //books not contains in db
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
                .build())
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(BookModelDto.class)
            .returnResult().getResponseBody();
        assertThat(response)
            .isEmpty();
    }

    @Test
    void searchByTitleWithEmptyNameMustReturn400() {
        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("books", "searchByTitle")
                .queryParam("name", " ")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
                .build())
            .exchange()
            .expectStatus().isEqualTo(400)
            .expectBody().jsonPath("$.errorList[0]").isEqualTo("3008");
    }

    @Test
    void searchWithFiltersShouldnWork() {
        final var user = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .map(UserDto::getLogin)
            .findAny()
            .orElseThrow();

        final var booksId = createAndSaveBooks(user);

        final var response = webClient
            .method(HttpMethod.POST)
            .uri(uriBuilder -> uriBuilder.pathSegment("books", "searchWithFilters").build())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(BookFiltersRequest
                .create("Novosibirsk", "Wolves", "author", List.of(2), "publishing_house", 2000, 0, 10))
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(BookModelDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .hasSize(1)
            .containsOnly(TestDataProvider.buildWolves(booksId.get(2)));
    }

    @Test
    void getBooksByUserShouldWork() {
        final var user = createAndSaveUser(TestDataProvider.buildAlex());
        createAndSaveBooks(user.getLogin());

        final var response = webClient
            .method(HttpMethod.GET)
            .uri(uriBuilder -> uriBuilder
                .pathSegment("books", "by-user")
                .queryParam("id", user.getUserId())
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
                .build())
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(BookModelDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .hasSize(3);
    }

    @Test
    void getBooksByUserShouldReturnErrorWhenUserIdIsNull() {
        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("books", "by-user")
                .queryParam("id", "  ")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
                .build())
            .exchange()
            .expectStatus().isEqualTo(400)
            .expectBody().jsonPath("$.errorList[0]").isEqualTo("3013");
    }
}
