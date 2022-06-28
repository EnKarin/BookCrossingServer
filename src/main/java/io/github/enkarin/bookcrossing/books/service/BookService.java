package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.books.repository.BookRepository;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BookService(final UserRepository usr, final BookRepository bkr, final ModelMapper mdm) {
        userRepository = usr;
        bookRepository = bkr;
        modelMapper = mdm;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        final TypeMap<BookDto, Book> bookDtoMapper = modelMapper.createTypeMap(BookDto.class, Book.class);
        bookDtoMapper.addMappings(ms -> ms.skip(Book::setOwner));
    }

    public BookModelDto saveBook(final BookDto bookDTO, final String login) {
        final Book book = convertToBook(bookDTO, login);
        return BookModelDto.fromBook(bookRepository.save(book));
    }

    public List<BookModelDto> findBookForOwner(final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        return bookRepository.findBooksByOwner(user).stream()
                .filter(b -> b.getOwner().isAccountNonLocked())
                .map(BookModelDto::fromBook)
                .collect(Collectors.toList());
    }

    public BookModelDto findById(final int bookId) {
        final Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
        return BookModelDto.fromBook(book);
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
                .map(BookModelDto::fromBook)
                .collect(Collectors.toList());
    }

    public List<BookModelDto> findAll() {
        return bookRepository.findAll().stream()
                .filter(b -> b.getOwner().isAccountNonLocked())
                .map(BookModelDto::fromBook)
                .collect(Collectors.toList());
    }

    public void deleteBook(final int bookId) {
        bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
        bookRepository.deleteById(bookId);
    }

    public List<BookModelDto> findByTitle(final String title) {
        return bookRepository.findBooksByTitleIgnoreCase(title).stream()
                .filter(b -> b.getOwner().isAccountNonLocked())
                .map(BookModelDto::fromBook)
                .collect(Collectors.toList());
    }

    private Book convertToBook(final BookDto bookDTO, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        final Book book = modelMapper.map(bookDTO, Book.class);
        book.setOwner(user);
        return book;
    }
}
