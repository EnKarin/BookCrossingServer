package io.github.enkarin.bookcrossing.user.service;

import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.books.repository.BookRepository;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookmarksService {

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    public boolean saveBookmarks(final int bookId, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        final Optional<Book> book = bookRepository.findById(bookId);
        if (book.isPresent()) {
            user.getBookmarks().add(book.get());
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteBookmarks(final int bookId, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        final Optional<Book> book = bookRepository.findById(bookId);
        if (book.isPresent()) {
            final Set<Book> bookMarks = user.getBookmarks();
            if (bookMarks.contains(book.get())) {
                user.getBookmarks().remove(book.get());
                userRepository.save(user);
            }
            return true;
        } else {
            return false;
        }
    }

    public Set<Book> getAll(final String login) {
        return userRepository.findByLogin(login).orElseThrow().getBookmarks();
    }
}
