package ru.bookcrossing.BookcrossingServer.service;

import ru.bookcrossing.BookcrossingServer.entity.Book;
import ru.bookcrossing.BookcrossingServer.model.DTO.BookDTO;
import ru.bookcrossing.BookcrossingServer.model.request.BookFiltersRequest;

import java.util.List;
import java.util.Optional;

public interface BookService {

    void saveBook(BookDTO bookDTO);

    void deleteBook(int id);

    List<Book> findByTitle(String title);

    List<Book> findBookForOwner();

    Optional<Book> findById(int id);

    List<Book> findAll();

    List<Book> filter(BookFiltersRequest request);
}
