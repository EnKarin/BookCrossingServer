package io.github.enkarin.bookcrossing.books.repository;

import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findBooksByTitleIgnoreCase(String title);

    List<Book> findBooksByOwner(User user);
}
