package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookServiceTest extends BookCrossingBaseTests {

    @Autowired
    private BookService bookService;

    @AfterEach
    void delete() {
        usersId.forEach(u -> userService.deleteUser(u));
        usersId.clear();
    }

    @Test
    void saveBook() {
        final UserDto user = userService.saveUser(TestDataProvider.buildAlex());
        usersId.add(user.getUserId());
        final BookModelDto book = bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin());
        assertThat(book)
                .isEqualTo(TestDataProvider.buildDorian(book.getBookId()));
    }

    @Test
    void saveExceptionTest() {
        assertThatThrownBy(() -> bookService.saveBook(TestDataProvider.buildDorian(), "users"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void findBookForOwnerTest() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
                .map(u -> userService.saveUser(u))
                .collect(Collectors.toList());
        users.forEach(u -> usersId.add(u.getUserId()));

        final List<BookDto> books = TestDataProvider.buildBooks();

        bookService.saveBook(books.get(0), users.get(0).getLogin());
        final int book1 = bookService.saveBook(books.get(1), users.get(1).getLogin()).getBookId();
        final int book2 = bookService.saveBook(books.get(2), users.get(1).getLogin()).getBookId();

        assertThat(bookService.findBookForOwner("alex"))
                .hasSize(2)
                .containsExactlyInAnyOrder(TestDataProvider.buildDandelion(book1),
                        TestDataProvider.buildWolves(book2));
    }

    @Test
    void findEmptyBookForOwnerTest() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
                .map(u -> userService.saveUser(u))
                .collect(Collectors.toList());
        users.forEach(u -> usersId.add(u.getUserId()));

        TestDataProvider.buildBooks().forEach(b -> bookService.saveBook(b, users.get(0).getLogin()));

        assertThat(bookService.findBookForOwner("alex")).isEmpty();
    }

    @Test
    void findByIdTest() {
        final UserDto user = userService.saveUser(TestDataProvider.buildBot());
        usersId.add(user.getUserId());
        final int book = TestDataProvider.buildBooks().stream()
                .map(b -> bookService.saveBook(b, user.getLogin()))
                        .collect(Collectors.toList()).get(0).getBookId();
        assertThat(bookService.findById(book))
                .usingRecursiveComparison()
                .isEqualTo(TestDataProvider.buildDorian(book));
    }

    @Test
    void findByIdExceptionTest() {
        assertThatThrownBy(() -> bookService.findById(Integer.MAX_VALUE))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Книга не найдена");
    }

    @Test
    void oneFilterTest() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
                .map(u -> userService.saveUser(u))
                .collect(Collectors.toList());
        users.forEach(u -> usersId.add(u.getUserId()));

        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book2 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin())
                .getBookId();
        final int book3 = bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin())
                .getBookId();

        assertThat(bookService.filter(BookFiltersRequest.create(null, null, "author",
                null, null, 0)))
                .hasSize(2)
                .containsExactlyInAnyOrder(TestDataProvider.buildWolves(book2), TestDataProvider.buildDorian(book3));
    }

    @Test
    void allFilterTest() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
                .map(u -> userService.saveUser(u))
                .collect(Collectors.toList());
        users.forEach(u -> usersId.add(u.getUserId()));

        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book2 =  bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        assertThat(bookService.filter(BookFiltersRequest.create("Novosibirsk", "Wolves",
                "author", "story", "publishing_house", 2000)))
                .hasSize(1)
                .containsOnly(TestDataProvider.buildWolves(book2));
    }

    @Test
    void filterLockedUserTest() {
        final UserDto user1 = userService.saveUser(TestDataProvider.buildBot());
        usersId.add(user1.getUserId());

        bookService.saveBook(TestDataProvider.buildDorian(), user1.getLogin());

        jdbcTemplate.update("update t_user set account_non_locked = 0 where user_id = " + user1.getUserId());

        assertThat(bookService.filter(BookFiltersRequest.create(null, null, null, null,
                "publishing_house", 0)))
                .isEmpty();
    }

    @Test
    void deleteBookTest() {
        final UserDto user1 = userService.saveUser(TestDataProvider.buildBot());
        usersId.add(user1.getUserId());

        final BookModelDto book = bookService.saveBook(TestDataProvider.buildWolves(), user1.getLogin());

        bookService.deleteBook(book.getBookId());
        assertThat(jdbcTemplate.queryForObject("select exists(select * from t_book where book_id = " +
                        book.getBookId() + ")",
                Boolean.class))
                .isFalse();
    }

    @Test
    void deleteBookExceptionTest() {
        assertThatThrownBy(() -> bookService.deleteBook(2))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Книга не найдена");
    }

    @Test
    void findAllEmptyList() {
        assertThat(bookService.findAll()).isEmpty();
    }

    @Test
    void findAll() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
                .map(u -> userService.saveUser(u))
                .collect(Collectors.toList());
        users.forEach(u -> usersId.add(u.getUserId()));

        final int book1 = bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin()).getBookId();
        final int book2 = bookService.saveBook(TestDataProvider.buildDandelion(), users.get(1).getLogin()).getBookId();
        final int book3 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();

        assertThat(bookService.findAll())
                .hasSize(3)
                .hasSameElementsAs(TestDataProvider.buildBookModels(book1, book2, book3));
    }

    @Test
    void findByTitleTest() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
                .map(u -> userService.saveUser(u))
                .collect(Collectors.toList());
        users.forEach(u -> usersId.add(u.getUserId()));

        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 =  bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        assertThat(bookService.findByTitle("Wolves"))
                .hasSize(1)
                .containsOnly(TestDataProvider.buildWolves(book1));
    }

    @Test
    void findByTitleEmptyTest() {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
                .map(u -> userService.saveUser(u))
                .collect(Collectors.toList());
        users.forEach(u -> usersId.add(u.getUserId()));

        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        assertThat(bookService.findByTitle("tit")).isEmpty();
    }
}
