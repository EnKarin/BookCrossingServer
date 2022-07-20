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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BookService(final UserRepository usr, final BookRepository bkr, final ModelMapper mdm) {
        userRepository = usr;
        bookRepository = bkr;
        modelMapper = mdm;
        modelMapper.createTypeMap(BookDto.class, Book.class).addMappings(ms -> ms.skip(Book::setOwner));
    }

    @Transactional
    public BookModelDto saveBook(final BookDto bookDTO, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        final Book book = modelMapper.map(bookDTO, Book.class);
        book.setOwner(user);
        return BookModelDto.fromBook(bookRepository.save(book));
    }

    public List<BookModelDto> findBookForOwner(final String login) {
        return bookRepository.findBooksByOwner(userRepository.findByLogin(login).orElseThrow()).stream()
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
            books = books.stream()
                    .filter(book -> book.getGenre() != null)
                    .filter(book -> book.getGenre().equalsIgnoreCase(request.getGenre()))
                    .collect(Collectors.toList());
        }
        if (request.getAuthor() != null) {
            books = books.stream()
                    .filter(book -> book.getAuthor() != null)
                    .filter(book -> book.getAuthor().equalsIgnoreCase(request.getAuthor()))
                    .collect(Collectors.toList());
        }
        if (request.getPublishingHouse() != null) {
            books = books.stream()
                    .filter(book -> book.getPublishingHouse() != null)
                    .filter(book -> book.getPublishingHouse().equalsIgnoreCase(request.getPublishingHouse()))
                    .collect(Collectors.toList());
        }
        if (request.getYear() != 0) {
            books = books.stream()
                    .filter(book -> book.getYear() == request.getYear())
                    .collect(Collectors.toList());
        }
        if (request.getTitle() != null) {
            books = books.stream()
                    .filter(book -> book.getTitle() != null)
                    .filter(book -> book.getTitle().equalsIgnoreCase(request.getTitle()))
                    .collect(Collectors.toList());
        }
        if (request.getCity() != null) {
            books = books.stream()
                    .filter(book -> book.getOwner().getCity() != null)
                    .filter(book -> book.getOwner().getCity().equalsIgnoreCase(request.getCity()))
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

    @Transactional
    public void deleteBook(final int bookId) {
        bookRepository.findById(bookId).ifPresentOrElse(
                b -> bookRepository.deleteById(bookId),
                () -> {
                    throw new BookNotFoundException();
                });
    }

    public List<BookModelDto> findByTitle(final String title) {
        return bookRepository.findBooksByTitleIgnoreCase(title).stream()
                .filter(b -> b.getOwner().isAccountNonLocked())
                .map(BookModelDto::fromBook)
                .collect(Collectors.toList());
    }
}
