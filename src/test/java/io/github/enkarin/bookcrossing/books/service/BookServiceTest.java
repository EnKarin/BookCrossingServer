package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

@Transactional
class BookServiceTest extends BookCrossingBaseTests {

    @Autowired
    private BookService bookService;

    @Test
    void saveBook() {
        final BookModelDto returned = bookService.saveBook(BookDto.create("title", "author", null,
                null, 2000), "admin");
        assertThat(returned)
                .usingRecursiveComparison()
                .isEqualTo(BookModelDto.create(1, "title", "author", null,
                        null, 2000, null));
    }

    @Test
    void saveExceptionTest() {
        assertThatThrownBy(() -> bookService.saveBook(BookDto.create("title", "author", null,
                null, 2000), "users")).isInstanceOf(UserNotFoundException.class);
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void findBookForOwnerTest() {
        final List<BookModelDto> returned = bookService.findBookForOwner("user");
        assertThat(returned).size().isEqualTo(2);
        assertThat(returned.get(0))
                .usingRecursiveComparison()
                .isEqualTo(BookModelDto.create(3, "title2", "author",
                        "genre", "publishing_house", 2020, null));
        assertThat(returned.get(1))
                .usingRecursiveComparison()
                .isEqualTo(BookModelDto.create(4, "title3", "author",
                        "genre2", "publishing_house", 2000, null));
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void findEmptyBookForOwnerTest() {
        final List<BookModelDto> returned = bookService.findBookForOwner("alex");
        assertThat(returned).size().isEqualTo(0);
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
        assertThatThrownBy(() -> bookService.findById(3)).isInstanceOf(BookNotFoundException.class);
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void oneFilterTest() {
        final List<BookModelDto> returned = bookService.filter(BookFiltersRequest
                .create(null, null, null, "author", null, 0));
        assertThat(returned).size().isEqualTo(2);
        assertThat(returned.get(0))
                .usingRecursiveComparison()
                .isEqualTo(BookModelDto.create(3, "title2", "author",
                        "genre", "publishing_house", 2020, null));
        assertThat(returned.get(1))
                .usingRecursiveComparison()
                .isEqualTo(BookModelDto.create(4, "title3", "author",
                        "genre2", "publishing_house", 2000, null));
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void allFilterTest() {
        final List<BookModelDto> returned = bookService.filter(BookFiltersRequest
                .create("Novosibirsk", "title2", "genre", "author",
                        "publishing_house", 2020));
        assertThat(returned).size().isEqualTo(1);
        assertThat(returned.get(0))
                .usingRecursiveComparison()
                .isEqualTo(BookModelDto.create(3, "title2", "author",
                        "genre", "publishing_house", 2020, null));
    }

    @Sql("classpath:db/scripts/insert_locked_user_and_book.sql")
    @Test
    void filterLockedUserTest() {
        assertThat(bookService.filter(BookFiltersRequest
                .create(null, null, null, null, "publishing_house", 0)))
                .size().isEqualTo(0);
    }

    //JDBCTemplate

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void deleteBookTest() {
        bookService.deleteBook(2);
        assertThatThrownBy(() -> bookService.findById(2)).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void deleteBookExceptionTest() {
        assertThatThrownBy(() -> bookService.deleteBook(2)).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void findAllEmptyList() {
        assertThat(bookService.findAll()).size().isEqualTo(0);
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void findAll() {
        final List<BookModelDto> returned = bookService.findAll();
        assertThat(returned).size().isEqualTo(3);
        assertThat(returned.get(0))
                .usingRecursiveComparison()
                .isEqualTo(BookModelDto.create(2, "title", null, null,
                null, 2022, null));
        assertThat(returned.get(1))
                .usingRecursiveComparison()
                .isEqualTo(BookModelDto.create(3, "title2", "author",
                        "genre", "publishing_house", 2020, null));
        assertThat(returned.get(2))
                .usingRecursiveComparison()
                .isEqualTo(BookModelDto.create(4, "title3", "author",
                        "genre2", "publishing_house", 2000, null));
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void findByTitleTest() {
        final List<BookModelDto> returned = bookService.findByTitle("title");
        assertThat(returned).size().isEqualTo(1);
        assertThat(returned.get(0))
                .usingRecursiveComparison()
                .isEqualTo(BookModelDto.create(2, "title", null, null,
                        null, 2022, null));
    }

    @SqlGroup({
        @Sql("classpath:db/scripts/insert_user.sql"),
        @Sql("classpath:db/scripts/insert_books.sql")
    })
    @Test
    void findByTitleEmptyTest() {
        assertThat(bookService.findByTitle("tit")).size().isEqualTo(0);
    }
}
