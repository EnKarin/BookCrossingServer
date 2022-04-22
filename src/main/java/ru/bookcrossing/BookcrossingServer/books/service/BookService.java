package ru.bookcrossing.BookcrossingServer.books.service;

import ru.bookcrossing.BookcrossingServer.books.dto.BookDTO;
import ru.bookcrossing.BookcrossingServer.books.model.Book;
import ru.bookcrossing.BookcrossingServer.books.request.BookFiltersRequest;
import ru.bookcrossing.BookcrossingServer.books.response.BookResponse;

import java.util.List;
import java.util.Optional;

public interface BookService {

    BookResponse saveBook(BookDTO bookDTO, String login);

    void deleteBook(int id);

    List<Book> findByTitle(String title);

    List<Book> findBookForOwner(String login);

    Optional<Book> findById(int id);

    List<Book> findAll();

    List<Book> filter(BookFiltersRequest request);
}
