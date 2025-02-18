package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.books.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutocompletionService {
    private final BookRepository bookRepository;

    public String[] autocompleteBookName(final String partName) {
        return bookRepository.findBooksByPartOfName(partName).stream()
            .map(Book::getTitle)
            .toArray(String[]::new);
    }

    public String[] autocompleteBookAuthor(final String partName) {
        return bookRepository.findBooksByPartOfAuthor(partName).stream()
            .map(Book::getAuthor)
            .toArray(String[]::new);
    }
}
