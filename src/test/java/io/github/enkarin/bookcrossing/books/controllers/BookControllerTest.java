package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.constant.ErrorMessage;
import io.github.enkarin.bookcrossing.registration.dto.UserRegistrationDto;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import io.github.enkarin.bookcrossing.user.dto.UserPublicProfileDto;
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
            .isEqualTo(TestDataProvider.buildBookModels(booksId.get(0), user.getCity(), booksId.get(1), user.getCity(), booksId.get(2), user.getCity()));
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
        assertThat(response).isEqualTo(TestDataProvider.buildDorian(bookId, user.getCity()));
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
            .isEqualTo(List.of(TestDataProvider.buildDandelion(firstBookId, user.getCity()), TestDataProvider.buildDandelion(secondBookId, user.getCity())));
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
            .containsOnly(TestDataProvider.buildDandelion(firstBookId, user.getCity()));
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
            .bodyValue(BookFiltersRequest.create(null, "Novosibirsk", "Wolves", "author", List.of(2),
                "publishing_house", 2000, 0, 10))
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(BookModelDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .hasSize(1)
            .containsOnly(TestDataProvider.buildWolves(booksId.get(2), "Novosibirsk"));
    }

    @Test
    void searchWithFiltersShouldnWorkWithFieldAuthorOrTitle() {
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
            .bodyValue(BookFiltersRequest.create("Wolves", null, null, null, List.of(), null, 0, 0, 10))
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(BookModelDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .hasSize(1)
            .containsOnly(TestDataProvider.buildWolves(booksId.get(2), "Novosibirsk"));
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

    @Test
    void searchBookOwner() {
        final UserRegistrationDto alex = TestDataProvider.buildAlex();
        final var user = createAndSaveUser(alex);
        final List<Integer> booksId = createAndSaveBooks(user.getLogin());

        final var result = webClient.get()
            .uri(uriBuilder -> uriBuilder.pathSegment("books", "owner")
                .queryParam("bookId", booksId.get(0))
                .queryParam("zoneId", 7)
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(UserPublicProfileDto.class).returnResult().getResponseBody();
        assertThat(result).satisfies(r -> {
            assertThat(r.getName()).isEqualTo(alex.getName());
            assertThat(r.getCity()).isEqualTo(alex.getCity());
        });
    }

    @Test
    void searchNotExistsBook() {
        webClient.get()
            .uri(uriBuilder -> uriBuilder.pathSegment("books", "owner")
                .queryParam("bookId", 110)
                .queryParam("zoneId", 7)
                .build())
            .exchange()
            .expectStatus().isNotFound()
            .expectBody().jsonPath("$.error").isEqualTo("1004");
    }
}
