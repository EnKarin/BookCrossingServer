package ru.bookcrossing.BookcrossingServer.books.service;

import ru.bookcrossing.BookcrossingServer.books.dto.BookDto;
import ru.bookcrossing.BookcrossingServer.books.dto.BookFiltersRequest;
import ru.bookcrossing.BookcrossingServer.books.dto.BookResponse;
import ru.bookcrossing.BookcrossingServer.books.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Optional<BookResponse> saveBook(BookDto bookDTO, String login);

    void deleteBook(int id);

    List<Book> findByTitle(String title);

    List<Book> findBookForOwner(String login);

    Optional<Book> findById(int id);

    List<Book> findAll();

    List<Book> filter(BookFiltersRequest request);
}
