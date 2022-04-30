package ru.bookcrossing.BookcrossingServer.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.books.model.Book;
import ru.bookcrossing.BookcrossingServer.books.repository.BookRepository;
import ru.bookcrossing.BookcrossingServer.user.model.User;
import ru.bookcrossing.BookcrossingServer.user.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookmarksService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public boolean saveBookmarks(int bookId, String login){
        User user = userRepository.findByLogin(login).orElseThrow();
        Optional<Book> book = bookRepository.findById(bookId);
        if(book.isPresent()){
            user.getBookmarks().add(book.get());
            userRepository.save(user);
            return true;
        }
        else return false;
    }

    public boolean deleteBookmarks(int bookId, String login){
        User user = userRepository.findByLogin(login).orElseThrow();
        Optional<Book> book = bookRepository.findById(bookId);
        if(book.isPresent()) {
            Set<Book> bookMarks = user.getBookmarks();
            if(bookMarks.contains(book.get())){
                user.getBookmarks().remove(book.get());
                userRepository.save(user);
            }
            return true;
        }
        else return false;
    }

    public Set<Book> getAll(String login){
        return userRepository.findByLogin(login).orElseThrow().getBookmarks();
    }
}
