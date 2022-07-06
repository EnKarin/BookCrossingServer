package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.registation.dto.UserDto;
import io.github.enkarin.bookcrossing.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        final User user = userService.saveUser(UserDto.create("Tester", "Test", "123456",
                "123456", "k.test@mail.ru", "NSK"));
        usersId.add(user.getUserId());
        final BookModelDto book = bookService.saveBook(BookDto.create("title", "author", null,
                        null, 2000), user.getLogin());
        assertThat(book)
                .isEqualTo(BookModelDto.create(book.getBookId(), "title", "author", null,
                        null, 2000, null));
    }

    @Test
    void saveExceptionTest() {
        assertThatThrownBy(() -> bookService.saveBook(BookDto.create("title", "author", null,
                null, 2000), "users"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void findBookForOwnerTest() {
        final User user1 = userService.saveUser(UserDto.create("Tester", "user", "123456",
                "123456", "k.test@mail.ru", "NSK"));
        final User user2 = userService.saveUser(UserDto.create("Tester", "alex", "123456",
                "123456", "kr.test@mail.ru", "NSK"));
        usersId.add(user1.getUserId());
        usersId.add(user2.getUserId());

        bookService.saveBook(BookDto.create("title", "author", null,
                null, 2000), user2.getLogin());
        final BookModelDto book1 = bookService.saveBook(BookDto.create("title2", "author",
                "genre", "publishing_house", 2020), user1.getLogin());
        final BookModelDto book2 = bookService.saveBook(BookDto.create("title3", "author",
                "genre2", "publishing_house", 2000), user1.getLogin());

        assertThat(bookService.findBookForOwner("user"))
                .hasSize(2)
                .containsExactlyInAnyOrder(BookModelDto.create(book1.getBookId(), "title2", "author",
                        "genre", "publishing_house", 2020, null),
                        BookModelDto.create(book2.getBookId(), "title3", "author",
                        "genre2", "publishing_house", 2000, null));
    }

    @Test
    void findEmptyBookForOwnerTest() {
        final User user1 = userService.saveUser(UserDto.create("Tester", "user", "123456",
                "123456", "k.test@mail.ru", "NSK"));
        final User user2 = userService.saveUser(UserDto.create("Tester", "alex", "123456",
                "123456", "kr.test@mail.ru", "NSK"));
        usersId.add(user1.getUserId());
        usersId.add(user2.getUserId());

        bookService.saveBook(BookDto.create("title2", "author",
                "genre", "publishing_house", 2020), user1.getLogin());
        bookService.saveBook(BookDto.create("title3", "author",
                "genre2", "publishing_house", 2000), user1.getLogin());

        assertThat(bookService.findBookForOwner("alex")).isEmpty();
    }

    @Test
    void findByIdTest() {
        final User user = userService.saveUser(UserDto.create("Tester", "user", "123456",
                "123456", "k.test@mail.ru", "NSK"));
        usersId.add(user.getUserId());
        final BookModelDto book = bookService.saveBook(BookDto.create("title", "author", null,
                null, 2000), user.getLogin());
        bookService.saveBook(BookDto.create("title3", "author",
                "genre2", "publishing_house", 2000), user.getLogin());

        assertThat(bookService.findById(book.getBookId()))
                .usingRecursiveComparison()
                .isEqualTo(BookModelDto.create(book.getBookId(), "title", "author", null,
                        null, 2000,  null));
    }

    @Test
    void findByIdExceptionTest() {
        assertThatThrownBy(() -> bookService.findById(Integer.MAX_VALUE))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Книга не найдена");
    }

    @Test
    void oneFilterTest() {
        final User user1 = userService.saveUser(UserDto.create("Tester", "user", "123456",
                "123456", "k.test@mail.ru", "NSK"));
        final User user2 = userService.saveUser(UserDto.create("Tester", "alex", "123456",
                "123456", "kr.test@mail.ru", "NSK"));
        usersId.add(user1.getUserId());
        usersId.add(user2.getUserId());

        bookService.saveBook(BookDto.create("title", "aut", null,
                null, 2000), user1.getLogin());
        final BookModelDto book2 = bookService.saveBook(BookDto.create("title2", "author",
                "genre", "publishing_house", 2020), user2.getLogin());
        final BookModelDto book3 = bookService.saveBook(BookDto.create("title3", "author",
                "genre2", "publishing_house", 2000), user1.getLogin());

        assertThat(bookService.filter(BookFiltersRequest.create(null, null, null, "author",
                null, 0)))
                .hasSize(2)
                .containsExactlyInAnyOrder(BookModelDto.create(book2.getBookId(), "title2", "author",
                        "genre", "publishing_house", 2020, null),
                        BookModelDto.create(book3.getBookId(), "title3", "author",
                        "genre2", "publishing_house", 2000, null));
    }

    @Test
    void allFilterTest() {
        final User user1 = userService.saveUser(UserDto.create("Tester", "user", "123456",
                "123456", "k.test@mail.ru", "Novosibirsk"));
        final User user2 = userService.saveUser(UserDto.create("Tester", "alex", "123456",
                "123456", "kr.test@mail.ru", "Novosibirsk"));
        usersId.add(user1.getUserId());
        usersId.add(user2.getUserId());

        bookService.saveBook(BookDto.create("title", "author", null,
                null, 2000), user1.getLogin());
        final BookModelDto book2 = bookService.saveBook(BookDto.create("title2", "author",
                "genre", "publishing_house", 2020), user2.getLogin());
        bookService.saveBook(BookDto.create("title3", "author",
                "genre2", "publishing_house", 2000), user1.getLogin());

        assertThat(bookService.filter(BookFiltersRequest.create("Novosibirsk", "title2", "genre",
                "author", "publishing_house", 2020)))
                .hasSize(1)
                .containsOnly(BookModelDto.create(book2.getBookId(), "title2", "author",
                        "genre", "publishing_house", 2020, null));
    }

    @Test
    void filterLockedUserTest() {
        final User user1 = userService.saveUser(UserDto.create("Tester", "user", "123456",
                "123456", "k.test@mail.ru", "Novosibirsk"));
        usersId.add(user1.getUserId());

        bookService.saveBook(BookDto.create("title", "author", null,
                null, 2000), user1.getLogin());

        jdbcTemplate.update("update t_user set account_non_locked = 0 where user_id = " + user1.getUserId());

        assertThat(bookService.filter(BookFiltersRequest.create(null, null, null, null,
                "publishing_house", 0)))
                .isEmpty();
    }

    @Test
    void deleteBookTest() {
        final User user1 = userService.saveUser(UserDto.create("Tester", "user", "123456",
                "123456", "k.test@mail.ru", "Novosibirsk"));
        usersId.add(user1.getUserId());

        final BookModelDto book = bookService.saveBook(BookDto.create("title", "author", null,
                null, 2000), user1.getLogin());

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
        final User user1 = userService.saveUser(UserDto.create("Tester", "user", "123456",
                "123456", "k.test@mail.ru", "NSK"));
        final User user2 = userService.saveUser(UserDto.create("Tester", "alex", "123456",
                "123456", "kr.test@mail.ru", "NSK"));
        usersId.add(user1.getUserId());
        usersId.add(user2.getUserId());

        final BookModelDto book1 = bookService.saveBook(BookDto.create("title", null, null,
                null, 2022), user1.getLogin());
        final BookModelDto book2 = bookService.saveBook(BookDto.create("title2", "author",
                "genre", "publishing_house", 2020), user2.getLogin());
        final BookModelDto book3 = bookService.saveBook(BookDto.create("title3", "author",
                "genre2", "publishing_house", 2000), user1.getLogin());

        assertThat(bookService.findAll())
                .hasSize(3)
                .containsExactlyInAnyOrder(BookModelDto.create(book1.getBookId(), "title", null, null,
                null, 2022, null),
                        BookModelDto.create(book2.getBookId(), "title2", "author",
                        "genre", "publishing_house", 2020, null),
                        BookModelDto.create(book3.getBookId(), "title3", "author",
                        "genre2", "publishing_house", 2000, null));
    }

    @Test
    void findByTitleTest() {
        final User user1 = userService.saveUser(UserDto.create("Tester", "user", "123456",
                "123456", "k.test@mail.ru", "NSK"));
        final User user2 = userService.saveUser(UserDto.create("Tester", "alex", "123456",
                "123456", "kr.test@mail.ru", "NSK"));
        usersId.add(user1.getUserId());
        usersId.add(user2.getUserId());

        final BookModelDto book1 = bookService.saveBook(BookDto.create("title", null, null,
                null, 2022), user1.getLogin());
        bookService.saveBook(BookDto.create("title2", "author",
                "genre", "publishing_house", 2020), user2.getLogin());
        bookService.saveBook(BookDto.create("title3", "author",
                "genre2", "publishing_house", 2000), user1.getLogin());

        assertThat(bookService.findByTitle("title"))
                .hasSize(1)
                .containsOnly(BookModelDto.create(book1.getBookId(), "title", null, null,
                        null, 2022, null));
    }

    @Test
    void findByTitleEmptyTest() {
        final User user1 = userService.saveUser(UserDto.create("Tester", "user", "123456",
                "123456", "k.test@mail.ru", "NSK"));
        final User user2 = userService.saveUser(UserDto.create("Tester", "alex", "123456",
                "123456", "kr.test@mail.ru", "NSK"));
        usersId.add(user1.getUserId());
        usersId.add(user2.getUserId());

        bookService.saveBook(BookDto.create("title", null, null,
                null, 2022), user1.getLogin());
        bookService.saveBook(BookDto.create("title2", "author",
                "genre", "publishing_house", 2020), user2.getLogin());
        bookService.saveBook(BookDto.create("title3", "author",
                "genre2", "publishing_house", 2000), user1.getLogin());

        assertThat(bookService.findByTitle("tit")).isEmpty();
    }
}
