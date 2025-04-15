package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.books.dto.ChangeBookDto;
import io.github.enkarin.bookcrossing.books.enums.Status;
import io.github.enkarin.bookcrossing.books.service.BookService;
import io.github.enkarin.bookcrossing.constant.ErrorMessage;
import io.github.enkarin.bookcrossing.registration.dto.UserRegistrationDto;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MyBookControllerTest extends BookCrossingBaseTests {

    @Autowired
    private BookService bookService;

    @Test
    void saveBookTest() {
        final int user = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(user);
        final BookModelDto bookDto = checkPost(
            generateAccessToken(TestDataProvider.buildAuthBot()),
            TestDataProvider.buildDorian(), 201)
            .expectBody(BookModelDto.class)
            .returnResult().getResponseBody();
        assertThat(bookDto)
            .isNotNull()
            .isEqualTo(TestDataProvider.buildDorian(bookDto.getBookId(), "Novosibirsk"));
    }

    @Test
    void saveBadBookTest() {
        final int user = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(user);
        final var map = checkPost(generateAccessToken(TestDataProvider.buildAuthBot()),
            TestDataProvider.prepareBook().author("").build(), 406)
            .expectBody(new ParameterizedTypeReference<Map<String, List<String>>>() {
            })
            .returnResult().getResponseBody();
        assertThat(map)
            .isNotEmpty()
            .satisfies(m -> assertThat(m.get("errorList")).containsExactlyInAnyOrder("3005", "3006"));
    }

    @Test
    void saveBookWithUnexpectedGenreIdMustThrowException() {
        final int user = createAndSaveUser(TestDataProvider.buildBot()).getUserId();
        enabledUser(user);

        checkPost(generateAccessToken(TestDataProvider.buildAuthBot()), TestDataProvider.prepareBook().genre(200).title("Книжное...").build(), 400)
            .expectBody()
            .jsonPath("$.error").isEqualTo(ErrorMessage.ERROR_2007.getCode());
    }

    @Test
    void bookListTest() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();
        enabledUser(users.get(0).getUserId());
        enabledUser(users.get(1).getUserId());

        final List<BookDto> books = TestDataProvider.buildBooks();
        bookService.saveBook(books.get(0), users.get(1).getLogin());
        final int book1 = bookService.saveBook(books.get(1), users.get(0).getLogin()).getBookId();
        final int book2 = bookService.saveBook(books.get(2), users.get(0).getLogin()).getBookId();

        final var response = checkGet(generateAccessToken(TestDataProvider.buildAuthBot())).expectBodyList(BookModelDto.class)
            .returnResult()
            .getResponseBody();
        assertThat(response)
            .hasSize(2)
            .containsExactlyInAnyOrder(TestDataProvider.buildDandelion(book1, users.get(0).getCity()), TestDataProvider.buildWolves(book2, users.get(0).getCity()));
    }

    @Test
    void getStatusesTest() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        final String accessToken = generateAccessToken(TestDataProvider.buildAuthBot());

        webClient.get()
            .uri("/user/myBook/status")
            .headers(headers -> headers.setBearerAuth(accessToken))
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Status.class)
            .hasSize(2)
            .contains(Status.GIVE, Status.EXCHANGES);
    }

    @Test
    void bookEmptyListTest() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();
        enabledUser(users.get(0).getUserId());
        enabledUser(users.get(1).getUserId());
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(1).getLogin());

        checkGet(generateAccessToken(TestDataProvider.buildAuthBot())).expectBodyList(BookModelDto.class)
            .hasSize(0);
    }

    @Test
    void deleteBookTest() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        final List<BookModelDto> book = TestDataProvider.buildBooks().stream()
            .map(b -> bookService.saveBook(b, user.getLogin()))
            .toList();
        checkDelete(generateAccessToken(TestDataProvider.buildAuthBot()), book.get(0).getBookId(), 200);
        assertThat(bookService.findBookForOwner(user.getLogin(), 0, 2)).hasSize(2);
        assertThat(jdbcTemplate.queryForObject("select exists(select * from bookcrossing.t_book where book_id=?)", Boolean.class, book.get(0).getBookId())).isFalse();
    }

    @Test
    void deleteBookNotFoundExceptionTest() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        checkDelete(generateAccessToken(TestDataProvider.buildAuthBot()),
            Integer.MAX_VALUE, 404)
            .expectBody()
            .jsonPath("$.error").isEqualTo(ErrorMessage.ERROR_1004.getCode());
    }

    @Test
    void partialPutBook() {
        final UserRegistrationDto alex = TestDataProvider.buildAlex();
        final var user = createAndSaveUser(alex);
        enabledUser(user.getUserId());
        final List<Integer> booksId = createAndSaveBooks(user.getLogin());
        final BookModelDto bookModelDto = bookService.findById(booksId.get(0));

        final var result = webClient.put()
            .uri(uriBuilder -> uriBuilder.pathSegment("user", "myBook").build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthAlex())))
            .bodyValue(ChangeBookDto.builder().bookId(booksId.get(0)).year(-3645).author("Yog Sotott").build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(BookModelDto.class).returnResult().getResponseBody();

        assertThat(result).satisfies(r -> {
            assertThat(r.getGenre()).isEqualTo(bookModelDto.getGenre());
            assertThat(r.getYear()).isEqualTo(-3645);
            assertThat(r.getTitle()).isEqualTo(bookModelDto.getTitle());
            assertThat(r.getPublishingHouse()).isEqualTo(bookModelDto.getPublishingHouse());
            assertThat(r.getAuthor()).isEqualTo("Yog Sotott");
        });
    }

    @Test
    void fullPutBook() {
        final UserRegistrationDto alex = TestDataProvider.buildAlex();
        final var user = createAndSaveUser(alex);
        enabledUser(user.getUserId());
        final List<Integer> booksId = createAndSaveBooks(user.getLogin());

        final var result = webClient.put()
            .uri("/user/myBook")
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthAlex())))
            .bodyValue(ChangeBookDto.builder()
                .bookId(booksId.get(0))
                .year(-3645)
                .author("Yog Sotott")
                .title("New name")
                .genre(31)
                .publishingHouse("New publish house")
                .statusId(Status.EXCHANGES.getId())
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(BookModelDto.class).returnResult().getResponseBody();

        assertThat(result).satisfies(r -> {
            assertThat(r.getGenre()).isEqualTo(31);
            assertThat(r.getYear()).isEqualTo(-3645);
            assertThat(r.getTitle()).isEqualTo("New name");
            assertThat(r.getPublishingHouse()).isEqualTo("New publish house");
            assertThat(r.getAuthor()).isEqualTo("Yog Sotott");
            assertThat(r.getStatusId()).isEqualTo(Status.EXCHANGES.getId());
        });
    }

    @Test
    void putBookNotChangeRequiredFieldIfItEmpty() {
        final UserRegistrationDto alex = TestDataProvider.buildAlex();
        final var user = createAndSaveUser(alex);
        enabledUser(user.getUserId());
        final List<Integer> booksId = createAndSaveBooks(user.getLogin());
        final BookModelDto bookModelDto = bookService.findById(booksId.get(0));

        final var result = webClient.put()
            .uri(uriBuilder -> uriBuilder.pathSegment("user", "myBook").build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthAlex())))
            .bodyValue(ChangeBookDto.builder().bookId(booksId.get(0)).title(" ").author("").build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(BookModelDto.class).returnResult().getResponseBody();

        assertThat(result).isEqualTo(bookModelDto);
    }

    @Test
    void putBookFromOtherUser() {
        final UserRegistrationDto alex = TestDataProvider.buildAlex();
        final var user = createAndSaveUser(alex);
        enabledUser(user.getUserId());
        final UserDto bot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(bot.getUserId());
        final List<Integer> booksId = createAndSaveBooks(user.getLogin());

        webClient.put()
            .uri(uriBuilder -> uriBuilder.pathSegment("user", "myBook").build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .bodyValue(ChangeBookDto.builder().bookId(booksId.get(0)).year(-3645).author("Yog Sotott").build())
            .exchange()
            .expectStatus().isNotFound()
            .expectBody().jsonPath("$.error").isEqualTo(ErrorMessage.ERROR_1004.getCode());
    }

    private WebTestClient.ResponseSpec checkPost(final String access, final Object body, final int status) {
        return webClient.post()
            .uri("/user/myBook")
            .headers(headers -> headers.setBearerAuth(access))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isEqualTo(status);
    }

    private WebTestClient.ResponseSpec checkGet(final String access) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "myBook")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
                .build())
            .headers(headers -> headers.setBearerAuth(access))
            .exchange()
            .expectStatus().isEqualTo(200);
    }

    private WebTestClient.ResponseSpec checkDelete(final String access, final int bookId, final int status) {
        return webClient.delete()
            .uri("/user/myBook?bookId={bookId}", bookId)
            .headers(headers -> headers.setBearerAuth(access))
            .exchange()
            .expectStatus().isEqualTo(status);
    }
}
