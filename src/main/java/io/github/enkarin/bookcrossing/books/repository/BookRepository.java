package io.github.enkarin.bookcrossing.books.repository;

import io.github.enkarin.bookcrossing.books.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findBooksByTitleIgnoreCase(String title);

    Optional<Book> findBooksByOwnerLoginAndBookId(String login, int id);

    List<Book> findBooksByOwnerUserId(int userId);

    @Query(value = "SELECT * FROM bookcrossing.t_book WHERE lower(title) LIKE lower(concat('%', ?1,'%')) LIMIT 5", nativeQuery = true)
    List<Book> findBooksByPartOfName(String partName);

    @Query(value = "SELECT * FROM bookcrossing.t_book WHERE author IS NOT NULL AND lower(author) LIKE lower(concat('%', ?1,'%')) LIMIT 5", nativeQuery = true)
    List<Book> findBooksByPartOfAuthor(String partName);
}
