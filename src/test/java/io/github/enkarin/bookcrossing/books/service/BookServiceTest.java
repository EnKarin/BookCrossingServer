package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookServiceTest extends BookCrossingBaseTests {

    @Autowired
    private BookService bookService;

    @AfterEach
    void delete() {
        jdbcTemplate.update("delete from t_book where book_id in (1, 2, 3, 4)");
        jdbcTemplate.update("delete from t_user_role where user_id in (50, 66)");
        jdbcTemplate.update("delete from t_user where user_id in (50, 66)");
    }

    @Test
    void saveBook() {
        assertThat(bookService.saveBook(BookDto.create("title", "author", null,
                null, 2000), "admin"))
                .isEqualTo(BookModelDto.create(1, "title", "author", null,
                        null, 2000, null));
    }

    @Test
    void saveExceptionTest() {
        assertThatThrownBy(() -> bookService.saveBook(BookDto.create("title", "author", null,
                null, 2000), "users"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void findBookForOwnerTest() {
        assertThat(bookService.findBookForOwner("user"))
                .hasSize(2)
                .containsExactlyInAnyOrder(BookModelDto.create(3, "title2", "author",
                        "genre", "publishing_house", 2020, null),
                        BookModelDto.create(4, "title3", "author",
                        "genre2", "publishing_house", 2000, null));
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void findEmptyBookForOwnerTest() {
        assertThat(bookService.findBookForOwner("alex")).isEmpty();
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void findByIdTest() {
        assertThat(bookService.findById(3))
                .usingRecursiveComparison()
                .isEqualTo(BookModelDto.create(3, "title2", "author",
                        "genre", "publishing_house", 2020, null));
    }

    @Test
    void findByIdExceptionTest() {
        assertThatThrownBy(() -> bookService.findById(Integer.MAX_VALUE))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Книга не найдена");
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void oneFilterTest() {
        assertThat(bookService.filter(BookFiltersRequest.create(null, null, null, "author",
                null, 0)))
                .hasSize(2)
                .containsExactlyInAnyOrder(BookModelDto.create(3, "title2", "author",
                        "genre", "publishing_house", 2020, null),
                        BookModelDto.create(4, "title3", "author",
                        "genre2", "publishing_house", 2000, null));
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void allFilterTest() {
        assertThat(bookService.filter(BookFiltersRequest.create("Novosibirsk", "title2", "genre",
                "author", "publishing_house", 2020)))
                .hasSize(1)
                .containsOnly(BookModelDto.create(3, "title2", "author",
                        "genre", "publishing_house", 2020, null));
    }

    @Sql("classpath:db/scripts/insert_locked_user_and_book.sql")
    @Test
    void filterLockedUserTest() {
        assertThat(bookService.filter(BookFiltersRequest.create(null, null, null, null,
                "publishing_house", 0)))
                .isEmpty();
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void deleteBookTest() {
        bookService.deleteBook(2);
        assertThat(jdbcTemplate.queryForObject("select exists(select * from t_book where book_id = 2)",
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

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void findAll() {
        assertThat(bookService.findAll())
                .hasSize(3)
                .containsExactlyInAnyOrder(BookModelDto.create(2, "title", null, null,
                null, 2022, null),
                        BookModelDto.create(3, "title2", "author",
                        "genre", "publishing_house", 2020, null),
                        BookModelDto.create(4, "title3", "author",
                        "genre2", "publishing_house", 2000, null));
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void findByTitleTest() {
        assertThat(bookService.findByTitle("title"))
                .hasSize(1)
                .containsOnly(BookModelDto.create(2, "title", null, null,
                        null, 2022, null));
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void findByTitleEmptyTest() {
        assertThat(bookService.findByTitle("tit")).isEmpty();
    }
}
