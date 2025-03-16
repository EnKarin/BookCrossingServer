package io.github.enkarin.bookcrossing.user.controllers;

import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.constant.ErrorMessage;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.service.BookmarksService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

class BookmarksControllerTest extends BookCrossingBaseTests {

    @Autowired
    private BookmarksService bookmarksService;

    @Test
    void saveBookmarksShouldnWork() {
        final var user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        final int booksId = createAndSaveBooks(user.getLogin()).stream().findAny().orElse(0);

        final var response = execute(HttpMethod.POST, generateAccessToken(TestDataProvider.buildAuthBot()), booksId, 201)
            .expectBodyList(BookModelDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .extracting(BookModelDto::getBookId)
            .contains(booksId);
    }

    @Test
    void saveBookmarksShouldnFailWithBookNotFound() {
        final var user = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(user);

        execute(HttpMethod.POST, generateAccessToken(TestDataProvider.buildAuthBot()), Integer.MAX_VALUE, 404)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(ErrorMessage.ERROR_1004.getCode());
    }

    @Test
    void deleteBookmarksShouldnWork() {
        final var user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        final int bookId = createAndSaveBooks(user.getLogin()).stream().findAny().orElse(0);
        bookmarksService.saveBookmarks(bookId, user.getLogin());

        execute(HttpMethod.DELETE, generateAccessToken(TestDataProvider.buildAuthBot()), bookId, 200);

        assertThat(bookmarksService.getAll(user.getLogin(), 0, 10)).isEmpty();
    }

    @Test
    void deleteBookmarksShouldnFailWithBookNotFound() {
        final var user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        final var bookId = createAndSaveBooks(user.getLogin()).stream().findAny().orElse(0);
        execute(HttpMethod.DELETE, generateAccessToken(TestDataProvider.buildAuthBot()), bookId, 404)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(ErrorMessage.ERROR_1004.getCode());
    }

    @Test
    void getAllShouldnWork() {
        final var user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        final var books = createAndSaveBooks(user.getLogin());
        books.forEach(b -> bookmarksService.saveBookmarks(b, user.getLogin()));
        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "bookmarks")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(BookModelDto.class)
            .returnResult().getResponseBody();
        assertThat(response)
            .hasSize(3)
            .usingRecursiveFieldByFieldElementComparatorIgnoringFields("bookId")
            .containsOnlyOnceElementsOf(TestDataProvider.buildBookModels(0, user.getCity(), 0, user.getCity(), 0, user.getCity()));
    }

    @Test
    void getAllWithPaginationShouldnWork() {
        final var user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        final var books = createAndSaveBooks(user.getLogin());
        books.forEach(b -> bookmarksService.saveBookmarks(b, user.getLogin()));
        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "bookmarks")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 1)
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(BookModelDto.class)
            .returnResult().getResponseBody();
        assertThat(response).hasSize(1);
    }

    private WebTestClient.ResponseSpec execute(final HttpMethod method, final String access, final int bookId, final int status) {
        return webClient
            .method(method)
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "bookmarks")
                .queryParam("bookId", bookId)
                .build())
            .headers(headers -> headers.setBearerAuth(access))
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(status);
    }
}
