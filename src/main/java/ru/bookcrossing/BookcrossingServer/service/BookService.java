package ru.bookcrossing.BookcrossingServer.service;

import ru.bookcrossing.BookcrossingServer.entity.Book;
import ru.bookcrossing.BookcrossingServer.model.DTO.BookDTO;

import java.util.List;

public interface BookService {

    void saveBook(BookDTO bookDTO);

    void deleteBook(int id);

    List<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);

    List<Book> findByGenre(String genre);

    List<Book> findAll();
}
