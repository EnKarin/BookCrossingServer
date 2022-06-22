package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookResponse;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.books.repository.BookRepository;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookService {

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    private final ModelMapper modelMapper;

    private TypeMap<BookDto, Book> bookDtoMapper;

    public Optional<BookResponse> saveBook(final BookDto bookDTO, final String login) {
        Optional<Book> book = convertToBook(bookDTO, login);
        if (book.isEmpty()) {
            return Optional.empty();
        }
        book = Optional.of(bookRepository.save(book.get()));
        final BookResponse response = new BookResponse(book.get());
        return Optional.of(response);
    }

    public List<Book> findBookForOwner(final String login) {
        final Optional<User> user = userRepository.findByLogin(login);
        List<Book> books;
        if (user.isPresent()) {
            books = bookRepository.findBooksByOwner(user.get());
            return books.stream().filter(b -> b.getOwner().isAccountNonLocked()).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public Optional<Book> findById(final int bookId) {
        return bookRepository.findById(bookId);
    }

    public List<Book> filter(final BookFiltersRequest request) {
        List<Book> books = bookRepository.findAll();
        if (request.getGenre() != null) {
            books = books.stream().filter(book -> book.getGenre().equals(request.getGenre()))
                    .collect(Collectors.toList());
        }
        if (request.getAuthor() != null) {
            books = books.stream().filter(book -> book.getAuthor().equals(request.getAuthor()))
                    .collect(Collectors.toList());
        }
        if (request.getPublishingHouse() != null) {
            books = books.stream().filter(book -> book.getPublishingHouse().equals(request.getPublishingHouse()))
                    .collect(Collectors.toList());
        }
        if (request.getYear() != 0) {
            books = books.stream().filter(book -> book.getYear() == request.getYear())
                    .collect(Collectors.toList());
        }
        if (request.getTitle() != null) {
            books = books.stream().filter(book -> book.getTitle().equals(request.getTitle()))
                    .collect(Collectors.toList());
        }
        if (request.getCity() != null) {
            books = books.stream().filter(book -> book.getOwner().getCity().equals(request.getCity()))
                    .collect(Collectors.toList());
        }
        return books.stream().filter(b -> b.getOwner().isAccountNonLocked()).collect(Collectors.toList());
    }

    public List<Book> findAll() {
        return bookRepository.findAll().stream()
                .filter(b -> b.getOwner().isAccountNonLocked())
                .collect(Collectors.toList());
    }

    public void deleteBook(final int bookId) {
        if (bookRepository.findById(bookId).isPresent()) {
            bookRepository.deleteById(bookId);
        }
    }

    public List<Book> findByTitle(final String title) {
        return bookRepository.findBooksByTitleIgnoreCase(title).stream()
               .filter(b -> b.getOwner().isAccountNonLocked())
               .collect(Collectors.toList());
    }

    private Optional<Book> convertToBook(final BookDto bookDTO, final String login) {
        final Optional<User> user = userRepository.findByLogin(login);
        if (bookDtoMapper == null) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
            bookDtoMapper = modelMapper.createTypeMap(BookDto.class, Book.class);
            bookDtoMapper.addMappings(ms -> ms.skip(Book::setOwner));
        }
        if (user.isPresent()) {
            final Book book = modelMapper.map(bookDTO, Book.class);
            book.setOwner(user.get());
            return Optional.of(book);
        }
        return Optional.empty();
    }
}
