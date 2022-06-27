package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
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

    public Optional<BookModelDto> saveBook(final BookDto bookDTO, final String login) {
        final Book book = convertToBook(bookDTO, login);
        if (book == null) {
            return Optional.empty();
        }
        return Optional.of(modelMapper.map(bookRepository.save(book), BookModelDto.class));
    }

    public Optional<List<BookModelDto>> findBookForOwner(final String login) {
        final Optional<User> user = userRepository.findByLogin(login);
        return user.map(value -> bookRepository.findBooksByOwner(value).stream()
                .filter(b -> b.getOwner().isAccountNonLocked())
                .map(b -> modelMapper.map(b, BookModelDto.class))
                .collect(Collectors.toList()));
    }

    public Optional<BookModelDto> findById(final int bookId) {
        final Optional<Book> book = bookRepository.findById(bookId);
        return book.map(value -> modelMapper.map(value, BookModelDto.class));
    }

    public List<BookModelDto> filter(final BookFiltersRequest request) {
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
        return books.stream()
                .filter(b -> b.getOwner().isAccountNonLocked())
                .map(b -> modelMapper.map(b, BookModelDto.class))
                .collect(Collectors.toList());
    }

    public List<BookModelDto> findAll() {
        return bookRepository.findAll().stream()
                .filter(b -> b.getOwner().isAccountNonLocked())
                .map(b -> modelMapper.map(b, BookModelDto.class))
                .collect(Collectors.toList());
    }

    public void deleteBook(final int bookId) {
        if (bookRepository.findById(bookId).isPresent()) {
            bookRepository.deleteById(bookId);
        }
    }

    public List<BookModelDto> findByTitle(final String title) {
        return bookRepository.findBooksByTitleIgnoreCase(title).stream()
                .filter(b -> b.getOwner().isAccountNonLocked())
                .map(b -> modelMapper.map(b, BookModelDto.class))
                .collect(Collectors.toList());
    }

    private Book convertToBook(final BookDto bookDTO, final String login) {
        final Optional<User> user = userRepository.findByLogin(login);
        if (bookDtoMapper == null) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
            bookDtoMapper = modelMapper.createTypeMap(BookDto.class, Book.class);
            bookDtoMapper.addMappings(ms -> ms.skip(Book::setOwner));
        }
        if (user.isPresent()) {
            final Book book = modelMapper.map(bookDTO, Book.class);
            book.setOwner(user.get());
            return book;
        }
        return null;
    }
}
