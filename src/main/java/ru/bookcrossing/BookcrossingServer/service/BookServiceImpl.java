package ru.bookcrossing.BookcrossingServer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.config.jwt.JwtProvider;
import ru.bookcrossing.BookcrossingServer.entity.Book;
import ru.bookcrossing.BookcrossingServer.model.DTO.BookDTO;
import ru.bookcrossing.BookcrossingServer.repository.BookRepository;
import ru.bookcrossing.BookcrossingServer.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService{

    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    @Override
    public void saveBook(BookDTO bookDTO) {
        String login = jwtProvider.getLoginFromToken();

        Book book = new Book();
        book.setAuthor(bookDTO.getAuthor());
        book.setGenre(bookDTO.getGenre());
        book.setTitle(bookDTO.getTitle());
        book.setPublishingHouse(bookDTO.getPublishingHouse());
        book.setYear(bookDTO.getYear());

        book.setOwner(userRepository.findByLogin(login));

        bookRepository.save(book);
    }

    @Override
    public List<Book> findAll() {
        String login = jwtProvider.getLoginFromToken();

        return bookRepository.findBooksByOwner(userRepository.findByLogin(login));
    }

    @Override
    public void deleteBook(int id) {
        if(bookRepository.findById(id).isPresent()) {
            bookRepository.deleteById(id);
        }
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
