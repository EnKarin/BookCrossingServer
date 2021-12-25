package ru.bookcrossing.BookcrossingServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.config.jwt.JwtProvider;
import ru.bookcrossing.BookcrossingServer.entity.Book;
import ru.bookcrossing.BookcrossingServer.model.DTO.BookDTO;
import ru.bookcrossing.BookcrossingServer.repository.BookRepository;
import ru.bookcrossing.BookcrossingServer.repository.UserRepository;

import java.util.List;

@Service
public class BookServiceImpl implements BookService{

    JwtProvider jwtProvider;

    UserRepository userRepository;

    BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(JwtProvider j, UserRepository r, BookRepository b){
        jwtProvider = j;
        userRepository = r;
        bookRepository = b;
    }

    @Override
    public boolean saveBook(BookDTO bookDTO) {
        String login = jwtProvider.getLoginFromToken();

        Book book = new Book();
        book.setAuthor(bookDTO.getAuthor());
        book.setGenre(bookDTO.getGenre());
        book.setTitle(bookDTO.getTitle());
        book.setPublishingHouse(bookDTO.getPublishingHouse());
        book.setYear(bookDTO.getYear());

        book.setOwner(userRepository.findByLogin(login));

        bookRepository.save(book);
        return true;
    }

    @Override
    public void deleteBook(BookDTO bookDTO) {

    }

    @Override
    public List<Book> findByTitle(String t) {
        return null;
    }

    @Override
    public List<Book> findByAuthor(String a) {
        return null;
    }

    @Override
    public List<Book> findByGenre(String g) {
        return null;
    }
}
