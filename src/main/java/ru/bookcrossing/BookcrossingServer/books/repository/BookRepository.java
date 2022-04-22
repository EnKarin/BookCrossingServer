package ru.bookcrossing.BookcrossingServer.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.BookcrossingServer.books.model.Book;
import ru.bookcrossing.BookcrossingServer.user.model.User;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findBooksByTitleIgnoreCase(String title);

    List<Book> findBooksByOwner(User user);

    void deleteByOwner(User user);
}
