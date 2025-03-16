package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.books.enums.Status;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.exception.GenreNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookServiceTest extends BookCrossingBaseTests {

    @Test
    void saveBookShouldWork() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final BookModelDto book = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin());
        assertThat(book)
            .isEqualTo(TestDataProvider.buildDorian(book.getBookId(), user.getCity()));
    }

    @Test
    void saveShouldFailWithUserNotFound() {
        final BookDto dto = TestDataProvider.buildDorian();
        assertThatThrownBy(() -> bookService.saveBook(dto, "users"))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("Пользователь не найден");
    }

    @Test
    void findBookForOwnerShouldWork() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();

        final List<BookDto> books = TestDataProvider.buildBooks();

        bookService.saveBook(books.get(0), users.get(0).getLogin());
        final int book1 = bookService.saveBook(books.get(1), users.get(1).getLogin()).getBookId();
        final int book2 = bookService.saveBook(books.get(2), users.get(1).getLogin()).getBookId();

        assertThat(bookService.findBookForOwner(users.get(1).getLogin(), 0, 3))
            .hasSize(2)
            .containsExactlyInAnyOrder(TestDataProvider.buildDandelion(book1, users.get(1).getCity()), TestDataProvider.buildWolves(book2, users.get(1).getCity()));
    }


    @Test
    void findBookWithPaginationForOwnerShouldWork() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();

        final List<BookDto> books = TestDataProvider.buildBooks();

        bookService.saveBook(books.get(0), users.get(0).getLogin());
        final int book1 = bookService.saveBook(books.get(1), users.get(1).getLogin()).getBookId();
        final int book2 = bookService.saveBook(books.get(2), users.get(1).getLogin()).getBookId();

        assertThat(bookService.findBookForOwner(users.get(1).getLogin(), 0, 1))
            .hasSize(1)
            .containsAnyOf(TestDataProvider.buildDandelion(book1, users.get(1).getCity()), TestDataProvider.buildWolves(book2, users.get(1).getCity()));
    }

    @Test
    void findBookForOwnerShouldWorkWithEmptyBookList() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();

        TestDataProvider.buildBooks().forEach(b -> bookService.saveBook(b, users.get(0).getLogin()));

        assertThat(bookService.findBookForOwner(users.get(1).getLogin(), 0, 10)).isEmpty();
    }

    @Test
    void findByIdShouldWork() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildBot());
        final int book = TestDataProvider.buildBooks().stream()
            .map(b -> bookService.saveBook(b, user.getLogin()))
            .toList().get(0).getBookId();
        assertThat(bookService.findById(book))
            .usingRecursiveComparison()
            .isEqualTo(TestDataProvider.buildDorian(book, user.getCity()));
    }

    @Test
    void findByUserIdShouldWork() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildBot());
        final int book = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        assertThat(bookService.findBookByOwnerId(String.valueOf(user.getUserId()), PageRequest.of(0, 10)))
            .hasSize(1)
            .first()
            .extracting(BookModelDto::getBookId)
            .isEqualTo(book);
    }

    @Test
    void findByUserIdWithPaginationShouldWork() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildBot());
        bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin());
        final int secondBook = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        assertThat(bookService.findBookByOwnerId(String.valueOf(user.getUserId()), PageRequest.of(1, 1)))
            .hasSize(1)
            .first()
            .extracting(BookModelDto::getBookId)
            .isEqualTo(secondBook);
    }

    @Test
    void findByIdShouldFailWithBookNotFound() {
        assertThatThrownBy(() -> bookService.findById(Integer.MAX_VALUE))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessage("Книга не найдена");
    }

    @Test
    void filterShouldWorkWithOneField() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();

        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book2 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin())
            .getBookId();
        final int book3 = bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin())
            .getBookId();

        assertThat(bookService.filter(BookFiltersRequest.create(null, null, null, "author", null, null, 0, 0, 10)))
            .hasSize(2)
            .containsExactlyInAnyOrder(TestDataProvider.buildWolves(book2, users.get(1).getCity()), TestDataProvider.buildDorian(book3, users.get(0).getCity()));
    }

    @Test
    void filterShouldWorkWithAllField() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();

        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book2 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        assertThat(bookService.filter(BookFiltersRequest
            .create(null, "Novosibirsk", "Wolves", "author", List.of(2), "publishing_house", 2000, 0, 10)))
            .hasSize(1)
            .containsOnly(TestDataProvider.buildWolves(book2, users.get(1).getCity()));
    }

    @Test
    void filterShouldWorkWithListGenre() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();

        final int book1 = bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin()).getBookId();
        final int book2 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        assertThat(bookService.filter(BookFiltersRequest.create(null, null, null, null, List.of(2, 3), null, 0, 0, 10)))
            .hasSize(2)
            .containsOnly(TestDataProvider.buildDandelion(book1, users.get(0).getCity()), TestDataProvider.buildWolves(book2, users.get(1).getCity()));
    }

    @Test
    void filterWithPaginationShouldWorkWithListGenre() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();

        final int book1 = bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        assertThat(bookService.filter(BookFiltersRequest.create(null, null, null, null, List.of(2, 3), null, 0, 0, 1)))
            .hasSize(1)
            .containsOnly(TestDataProvider.buildDandelion(book1, users.get(0).getCity()));
    }

    @ParameterizedTest
    @MethodSource("provideFilter")
    void filterShouldWorkWithNotSingleBookThatPassedFilter(final BookDto bookDto) {
        final var user = createAndSaveUser(TestDataProvider.prepareUser()
            .city(null)
            .login("Bot")
            .email("k.test@mail.ru")
            .build());
        bookService.saveBook(bookDto, user.getLogin());

        assertThat(bookService.filter(BookFiltersRequest
            .create(null, "Novosibirsk", "Wolves", "author", List.of(2), "publishing_house", 2000, 0, 10)))
            .isEmpty();
    }

    static Stream<BookDto> provideFilter() {
        return Stream.of(
            BookDto.builder().genre(2).title("Wolves").build(),
            BookDto.builder().genre(2).author("author").title("Wolves").build(),
            BookDto.builder().genre(2).author("author").publishingHouse("publishing_house").title("Wolves").build(),
            BookDto.builder().genre(2).author("author").publishingHouse("publishing_house").year(2000).title("Wolves").build(),
            BookDto.builder().genre(2).author("author").publishingHouse("publishing_house").year(2000).title("Wolves").build()
        );
    }

    @Test
    void filterLockedUserShouldWork() {
        final UserDto user1 = createAndSaveUser(TestDataProvider.buildBot());

        bookService.saveBook(TestDataProvider.buildDorian(), user1.getLogin());

        jdbcTemplate.update("update bookcrossing.t_user set account_non_locked = false where user_id = ?", user1.getUserId());

        assertThat(bookService.filter(BookFiltersRequest
            .create(null, null, null, null, List.of(0), "publishing_house", 0, 0, 10)))
            .isEmpty();
    }

    @Test
    void deleteBookShouldWork() {
        final UserDto user1 = createAndSaveUser(TestDataProvider.buildBot());

        final BookModelDto book = bookService.saveBook(TestDataProvider.buildWolves(), user1.getLogin());

        bookService.deleteBook(user1.getLogin(), book.getBookId());
        assertThat(jdbcTemplate.queryForObject("select exists(select * from bookcrossing.t_book where book_id = ?)",
            Boolean.class, book.getBookId()))
            .isFalse();
    }

    @Test
    void deleteBookShouldFailWithBookNotFound() {
        final UserDto user1 = createAndSaveUser(TestDataProvider.buildBot());

        assertThatThrownBy(() -> bookService.deleteBook(user1.getLogin(), 2))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessage("Книга не найдена");
    }

    @Test
    void deleteBookFromOtherUserMustThrowException() {
        final UserDto user1 = createAndSaveUser(TestDataProvider.buildBot());
        bookService.saveBook(TestDataProvider.buildDorian(), user1.getLogin());
        final UserDto user2 = createAndSaveUser(TestDataProvider.buildAlex());

        assertThatThrownBy(() -> bookService.deleteBook(user2.getLogin(), 2))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessage("Книга не найдена");
    }

    @Test
    void findAllShouldWorkWithEmptyBookList() {
        assertThat(bookService.findAll(0, 10)).isEmpty();
    }

    @Test
    void findAllShouldWork() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();

        final int book1 = bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin()).getBookId();
        final int book2 = bookService.saveBook(TestDataProvider.buildDandelion(), users.get(1).getLogin()).getBookId();
        final int book3 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();

        assertThat(bookService.findAll(0, 10))
            .hasSize(3)
            .hasSameElementsAs(TestDataProvider.buildBookModels(book1, users.get(0).getCity(), book2, users.get(2).getCity(), book3, users.get(1).getCity()));
    }

    @Test
    void findAllWithPaginationShouldWork() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();

        final int book1 = bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin());

        assertThat(bookService.findAll(0, 1))
            .hasSize(1)
            .extracting(BookModelDto::getBookId)
            .containsOnly(book1);
    }

    @Test
    void findByTitleShouldWork() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();

        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        assertThat(bookService.findByTitleOrAuthor("Wolves", 0, 10))
            .hasSize(1)
            .containsOnly(TestDataProvider.buildWolves(book1, users.get(0).getCity()));
    }

    @Test
    void findByAuthorShouldWork() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();

        final int bookid = bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin()).getBookId();

        assertThat(bookService.findByTitleOrAuthor("AUTHOR2", 0, 10))
            .hasSize(1)
            .first()
            .isEqualTo(TestDataProvider.buildDandelion(bookid, users.get(0).getCity()));
    }

    @Test
    void findByTitleShouldWorkWithBookNotFound() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();

        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        assertThat(bookService.findByTitleOrAuthor("tit", 0, 10)).isEmpty();
    }

    @Test
    void findBookOwner() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final int bookId = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        assertThat(Integer.parseInt(bookService.findBookOwner(bookId, 7).getUserId())).isEqualTo(user.getUserId());
    }

    @Test
    void putBookTitle() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final int bookId = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        bookService.changeBookTitle(user.getLogin(), bookId, "Redacted");

        assertThat(bookService.findById(bookId).getTitle()).isEqualTo("Redacted");
    }

    @Test
    void putBookTitleFromNotOwnerMustThrowException() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final UserDto max = createAndSaveUser(TestDataProvider.buildMax());
        final int bookId = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        assertThatThrownBy(() -> bookService.changeBookTitle(max.getLogin(), bookId, "Redacted")).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void putBookAuthor() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final int bookId = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        bookService.changeBookAuthor(user.getLogin(), bookId, "Redacted");

        assertThat(bookService.findById(bookId).getAuthor()).isEqualTo("Redacted");
    }

    @Test
    void putBookAuthorFromNotOwnerMustThrowException() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final UserDto max = createAndSaveUser(TestDataProvider.buildMax());
        final int bookId = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        assertThatThrownBy(() -> bookService.changeBookAuthor(max.getLogin(), bookId, "Redacted")).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void putBookPublishHouse() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final int bookId = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        bookService.changeBookPublishingHouse(user.getLogin(), bookId, "Redacted");

        assertThat(bookService.findById(bookId).getPublishingHouse()).isEqualTo("Redacted");
    }

    @Test
    void putBookPublishHouseFromNotOwnerMustThrowException() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final UserDto max = createAndSaveUser(TestDataProvider.buildMax());
        final int bookId = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        assertThatThrownBy(() -> bookService.changeBookPublishingHouse(max.getLogin(), bookId, "Redacted")).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void putBookYear() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final int bookId = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        bookService.changeBookYear(user.getLogin(), bookId, 100);

        assertThat(bookService.findById(bookId).getYear()).isEqualTo(100);
    }

    @Test
    void putBookStatus() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final int bookId = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        bookService.changeBookStatus(user.getLogin(), bookId, Status.GIVES);

        assertThat(bookService.findById(bookId).getStatus()).isEqualTo(Status.GIVES);
    }

    @Test
    void putBookYearFromNotOwnerMustThrowException() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final UserDto max = createAndSaveUser(TestDataProvider.buildMax());
        final int bookId = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        assertThatThrownBy(() -> bookService.changeBookYear(max.getLogin(), bookId, 200)).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void putBookGenre() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final int bookId = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        bookService.changeBookGenre(user.getLogin(), bookId, 10);

        assertThat(bookService.findById(bookId).getGenre()).isEqualTo(10);
    }

    @Test
    void putBookGenreFromNotOwnerMustThrowException() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final UserDto max = createAndSaveUser(TestDataProvider.buildMax());
        final int bookId = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        assertThatThrownBy(() -> bookService.changeBookGenre(max.getLogin(), bookId, 20)).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void putBookIncorrectGenreMushThrowException() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildAlex());
        final int bookId = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin()).getBookId();

        assertThatThrownBy(() -> bookService.changeBookGenre(user.getLogin(), bookId, 500)).isInstanceOf(GenreNotFoundException.class);
    }
}
