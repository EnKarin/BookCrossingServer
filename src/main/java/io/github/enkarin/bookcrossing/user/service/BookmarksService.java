package io.github.enkarin.bookcrossing.user.service;

import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.books.repository.BookRepository;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookmarksService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public List<BookModelDto> saveBookmarks(final int bookId, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        final Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
        user.getBookmarks().add(book);
        return userRepository.save(user).getBookmarks().stream()
            .map(BookModelDto::fromBook)
            .toList();
    }

    @Transactional
    public boolean deleteBookmarks(final int bookId, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        final Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
        final Set<Book> bookMarks = user.getBookmarks();
        if (bookMarks.contains(book)) {
            user.getBookmarks().remove(book);
            userRepository.save(user);
            return true;
        }
        throw new BookNotFoundException();
    }

    public List<BookModelDto> getAll(final String login, final int pageNumber, final int pageSize) {
        return userRepository.findByLogin(login).orElseThrow().getBookmarks().stream()
            .skip((long) pageNumber * pageSize)
            .limit(pageSize)
            .map(BookModelDto::fromBook)
            .toList();
    }
}
