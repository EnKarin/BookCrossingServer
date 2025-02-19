package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.books.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutocompletionService {
    private final BookRepository bookRepository;

    public List<String> autocompleteBookNameOrAuthor(final String partName) {
        final List<String> result = bookRepository.findBooksByPartOfNameOrAuthor(partName).stream()
            .map(Book::getTitle)
            .collect(Collectors.toList());
        result.addAll(bookRepository.findBooksByPartOfAuthor(partName).stream()
            .map(Book::getAuthor)
            .toList());
        return result.subList(0, Math.min(result.size(), 5));
    }
}
