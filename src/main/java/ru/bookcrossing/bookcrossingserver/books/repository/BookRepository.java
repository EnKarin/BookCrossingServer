package ru.bookcrossing.bookcrossingserver.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.bookcrossingserver.books.model.Book;
import ru.bookcrossing.bookcrossingserver.user.model.User;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findBooksByTitleIgnoreCase(String title);

    List<Book> findBooksByOwner(User user);

    void deleteByOwner(User user);
}
