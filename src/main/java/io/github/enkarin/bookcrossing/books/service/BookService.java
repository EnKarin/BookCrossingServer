package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.books.repository.BookRepository;
import io.github.enkarin.bookcrossing.books.repository.GenreRepository;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.exception.GenreNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BookService(final UserRepository usr, final BookRepository bkr, final ModelMapper mdm, final GenreRepository genreRepository) {
        userRepository = usr;
        bookRepository = bkr;
        modelMapper = mdm;
        modelMapper.createTypeMap(BookDto.class, Book.class).addMappings(ms -> ms.skip(Book::setOwner));
        this.genreRepository = genreRepository;
    }

    @Transactional
    public BookModelDto saveBook(final BookDto bookDTO, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        final Book book = modelMapper.map(bookDTO, Book.class);
        book.setOwner(user);
        book.setGenre(genreRepository.findById(bookDTO.getGenre()).orElseThrow(GenreNotFoundException::new));
        return BookModelDto.fromBook(bookRepository.save(book));
    }

    public List<BookModelDto> findBookForOwner(final String login) {
        return userRepository.findByLogin(login).orElseThrow().getBooks().stream()
            .map(BookModelDto::fromBook)
            .toList();
    }

    public List<BookModelDto> findBookByOwnerId(final String userId) {
        return bookRepository.findBooksByOwnerUserId(Integer.parseInt(userId)).stream()
            .map(BookModelDto::fromBook)
            .toList();
    }

    public BookModelDto findById(final int bookId) {
        final Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
        return BookModelDto.fromBook(book);
    }

    public List<BookModelDto> filter(final BookFiltersRequest request) {
        List<Book> books = bookRepository.findAll();
        if (!CollectionUtils.isEmpty(request.getGenre())) {
            books = books.stream()
                .filter(book -> request.getGenre().contains(book.getGenre().getId()))
                .toList();
        }
        if (request.getAuthor() != null) {
            books = books.stream()
                .filter(book -> book.getAuthor() != null)
                .filter(book -> book.getAuthor().equalsIgnoreCase(request.getAuthor()))
                .toList();
        }
        if (request.getPublishingHouse() != null) {
            books = books.stream()
                .filter(book -> book.getPublishingHouse() != null)
                .filter(book -> book.getPublishingHouse().equalsIgnoreCase(request.getPublishingHouse()))
                .toList();
        }
        if (request.getYear() != 0) {
            books = books.stream()
                .filter(book -> book.getYear() == request.getYear())
                .toList();
        }
        if (request.getTitle() != null) {
            books = books.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(request.getTitle()))
                .toList();
        }
        if (request.getCity() != null) {
            books = books.stream()
                .filter(book -> book.getOwner().getCity() != null)
                .filter(book -> book.getOwner().getCity().equalsIgnoreCase(request.getCity()))
                .toList();
        }
        return books.stream()
            .filter(b -> b.getOwner().isAccountNonLocked())
            .map(BookModelDto::fromBook)
            .toList();
    }

    public List<BookModelDto> findAll() {
        return bookRepository.findAll().stream()
            .filter(b -> b.getOwner().isAccountNonLocked())
            .map(BookModelDto::fromBook)
            .toList();
    }

    @Transactional
    public void deleteBook(final int bookId) {
        bookRepository.findById(bookId).ifPresentOrElse(
            b -> bookRepository.deleteById(bookId),
            () -> {
                throw new BookNotFoundException();
            });
    }

    public List<BookModelDto> findByTitleOrAuthor(final String field) {
        return bookRepository.findBooksByTitleOrAuthorIgnoreCase(field).stream()
            .filter(b -> b.getOwner().isAccountNonLocked())
            .map(BookModelDto::fromBook)
            .toList();
    }
}
