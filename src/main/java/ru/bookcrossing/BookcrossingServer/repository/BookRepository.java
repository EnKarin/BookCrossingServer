package ru.bookcrossing.BookcrossingServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookcrossing.BookcrossingServer.entity.Book;
import ru.bookcrossing.BookcrossingServer.entity.User;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findBooksByAuthor(String author);

    List<Book> findBooksByTitle(String title);

    List<Book> findBooksByGenre(String genre);

    List<Book> findBooksByOwner(User user);

}
