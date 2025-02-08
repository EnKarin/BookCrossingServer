package io.github.enkarin.bookcrossing.books.repository;

import io.github.enkarin.bookcrossing.books.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findBooksByTitleIgnoreCase(String title);

    Optional<Book> findBooksByOwnerLoginAndBookId(String login, int id);

    List<Book> findBooksByOwnerUserId(int userId);
}
