package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class AutocompletionServiceTest extends BookCrossingBaseTests {
    @Autowired
    private AutocompletionService autocompletionService;

    @Test
    void autocompleteBookName() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildBot());
        bookService.saveBook(TestDataProvider.buildDandelion(), user.getLogin());
        bookService.saveBook(TestDataProvider.buildWolves(), user.getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin());

        assertThat(autocompletionService.autocompleteBookNameOrAuthor("an")).containsOnly("Dandelion", "Dorian");
    }

    @Test
    void autocompleteBookNameWithManyResult() {
        largeInitBook();

        assertThat(autocompletionService.autocompleteBookNameOrAuthor("a")).hasSize(5);
    }

    @Test
    void autocompleteBookAuthor() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildBot());
        bookService.saveBook(TestDataProvider.buildDandelion(), user.getLogin());
        bookService.saveBook(TestDataProvider.buildWolves(), user.getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin());

        assertThat(autocompletionService.autocompleteBookNameOrAuthor("2")).containsOnly("author2");
    }

    @Test
    void autocompleteBookAuthorWithManyResult() {
        largeInitBook();

        assertThat(autocompletionService.autocompleteBookNameOrAuthor("author")).hasSize(5);
    }

    @Test
    void findAuthorsByTitleOrAuthorShouldWork() {
        largeInitBook();

        assertThat(autocompletionService.findAuthorNamesByTitleOrAuthor("an")).containsOnly("author", "author5", "author2");
    }

    @Test
    void findAuthorNamesByTitleOrAuthorsShouldWorkWithBookNotFound() {
        largeInitBook();

        assertThat(autocompletionService.findAuthorNamesByTitleOrAuthor("tit")).isEmpty();
    }

    private void largeInitBook() {
        final UserDto user = createAndSaveUser(TestDataProvider.buildBot());
        bookService.saveBook(TestDataProvider.buildDandelion(), user.getLogin());
        bookService.saveBook(TestDataProvider.buildWolves(), user.getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), user.getLogin());
        bookService.saveBook(TestDataProvider.prepareBook().title("Dragon land").genre(3).author("author2").year(2020).build(), user.getLogin());
        bookService.saveBook(TestDataProvider.prepareBook().title("Castle").genre(3).author("author3").year(2020).build(), user.getLogin());
        bookService.saveBook(TestDataProvider.prepareBook().title("Attention!").genre(3).author("author4").year(2020).build(), user.getLogin());
        bookService.saveBook(TestDataProvider.prepareBook().title("God of war").genre(3).author("author5").year(2020).build(), user.getLogin());
        bookService.saveBook(TestDataProvider.prepareBook().title("Undergrowth").genre(3).author("author5").year(2020).build(), user.getLogin());
        bookService.saveBook(TestDataProvider.prepareBook().title("Unanimous").genre(3).author("author5").year(2020).build(), user.getLogin());
        bookService.saveBook(TestDataProvider.prepareBook().title("Anon").genre(3).author("author5").year(2020).build(), user.getLogin());
    }
}
