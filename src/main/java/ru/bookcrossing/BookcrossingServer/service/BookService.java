package ru.bookcrossing.BookcrossingServer.service;

import ru.bookcrossing.BookcrossingServer.entity.Book;
import ru.bookcrossing.BookcrossingServer.model.DTO.BookDTO;

import java.util.List;

public interface BookService {

    void saveBook(BookDTO bookDTO);

    void deleteBook(BookDTO bookDTO);

    List<Book> findByTitle(String t);

    List<Book> findByAuthor(String a);

    List<Book> findByGenre(String g);

    List<Book> findAll();
}
