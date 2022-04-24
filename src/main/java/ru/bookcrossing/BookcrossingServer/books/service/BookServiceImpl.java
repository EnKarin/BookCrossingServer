package ru.bookcrossing.BookcrossingServer.books.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.books.dto.BookDto;
import ru.bookcrossing.BookcrossingServer.books.model.Book;
import ru.bookcrossing.BookcrossingServer.books.repository.BookRepository;
import ru.bookcrossing.BookcrossingServer.books.request.BookFiltersRequest;
import ru.bookcrossing.BookcrossingServer.books.response.BookResponse;
import ru.bookcrossing.BookcrossingServer.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService{

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;
    private TypeMap<BookDto, Book> bookDtoMapper = null;

    @Override
    public BookResponse saveBook(BookDto bookDTO, String login) {
        Book book = convertToBook(bookDTO, login);
        book = bookRepository.save(book);
        return new BookResponse(book);
    }

    @Override
    public List<Book> findBookForOwner(String login) {
        List<Book> books = bookRepository.findBooksByOwner(userRepository.findByLogin(login));
        return books.stream().filter(b -> b.getOwner().isAccountNonLocked()).collect(Collectors.toList());
    }

    @Override
    public Optional<Book> findById(int id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> filter(BookFiltersRequest request) {
        List<Book> books = bookRepository.findAll();
        if(request.getGenre() != null)
            books = books.stream().filter(book -> book.getGenre().equals(request.getGenre()))
        .collect(Collectors.toList());
        if(request.getAuthor() != null)
            books = books.stream().filter(book -> book.getAuthor().equals(request.getAuthor()))
                    .collect(Collectors.toList());
        if(request.getPublishingHouse() != null)
            books = books.stream().filter(book -> book.getPublishingHouse().equals(request.getPublishingHouse()))
                    .collect(Collectors.toList());
        if(request.getYear() != 0)
            books = books.stream().filter(book -> book.getYear() == (request.getYear()))
                    .collect(Collectors.toList());
        if(request.getTitle() != null)
            books = books.stream().filter(book -> book.getTitle().equals(request.getTitle()))
                    .collect(Collectors.toList());
        if(request.getCity() != null)
            books = books.stream().filter(book -> book.getOwner().getCity().equals(request.getCity()))
                    .collect(Collectors.toList());
        return books.stream().filter(b -> b.getOwner().isAccountNonLocked()).collect(Collectors.toList());
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll().stream()
                .filter(b -> b.getOwner().isAccountNonLocked())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBook(int id) {
        if(bookRepository.findById(id).isPresent()) {
            bookRepository.deleteById(id);
        }
    }

    @Override
    public List<Book> findByTitle(String t) {
       return bookRepository.findBooksByTitleIgnoreCase(t).stream()
               .filter(b -> b.getOwner().isAccountNonLocked())
               .collect(Collectors.toList());
    }

    private Book convertToBook(BookDto bookDTO, String login){
        if(bookDtoMapper == null){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
            bookDtoMapper = modelMapper.createTypeMap(BookDto.class, Book.class);
            bookDtoMapper.addMappings(ms -> ms.skip(Book::setOwner));
            }
        Book book = modelMapper.map(bookDTO, Book.class);
        book.setOwner(userRepository.findByLogin(login));
        return book;
    }
}
