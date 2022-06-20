package ru.bookcrossing.BookcrossingServer.books.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.books.dto.BookDto;
import ru.bookcrossing.BookcrossingServer.books.dto.BookFiltersRequest;
import ru.bookcrossing.BookcrossingServer.books.dto.BookResponse;
import ru.bookcrossing.BookcrossingServer.books.model.Book;
import ru.bookcrossing.BookcrossingServer.books.repository.BookRepository;
import ru.bookcrossing.BookcrossingServer.user.model.User;
import ru.bookcrossing.BookcrossingServer.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookService{

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;
    private TypeMap<BookDto, Book> bookDtoMapper = null;

    public Optional<BookResponse> saveBook(BookDto bookDTO, String login) {
        Optional<Book> book = convertToBook(bookDTO, login);
        if(book.isEmpty()){
            return Optional.empty();
        }
        book = Optional.of(bookRepository.save(book.get()));
        BookResponse response = new BookResponse(book.get());
        return Optional.of(response);
    }

    public List<Book> findBookForOwner(String login) {
        Optional<User> user = userRepository.findByLogin(login);
        List<Book> books;
        if (user.isPresent()) {
            books = bookRepository.findBooksByOwner(user.get());
            return books.stream().filter(b -> b.getOwner().isAccountNonLocked()).collect(Collectors.toList());
        }
        else return null;
    }

    public Optional<Book> findById(int id) {
        return bookRepository.findById(id);
    }

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
            books = books.stream().filter(book -> book.getYear() == request.getYear())
                    .collect(Collectors.toList());
        if(request.getTitle() != null)
            books = books.stream().filter(book -> book.getTitle().equals(request.getTitle()))
                    .collect(Collectors.toList());
        if(request.getCity() != null)
            books = books.stream().filter(book -> book.getOwner().getCity().equals(request.getCity()))
                    .collect(Collectors.toList());
        return books.stream().filter(b -> b.getOwner().isAccountNonLocked()).collect(Collectors.toList());
    }

    public List<Book> findAll() {
        return bookRepository.findAll().stream()
                .filter(b -> b.getOwner().isAccountNonLocked())
                .collect(Collectors.toList());
    }

    public void deleteBook(int id) {
        if(bookRepository.findById(id).isPresent()) {
            bookRepository.deleteById(id);
        }
    }

    public List<Book> findByTitle(String t) {
       return bookRepository.findBooksByTitleIgnoreCase(t).stream()
               .filter(b -> b.getOwner().isAccountNonLocked())
               .collect(Collectors.toList());
    }

    private Optional<Book> convertToBook(BookDto bookDTO, String login){
        Optional<User> user = userRepository.findByLogin(login);
        if(bookDtoMapper == null){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
            bookDtoMapper = modelMapper.createTypeMap(BookDto.class, Book.class);
            bookDtoMapper.addMappings(ms -> ms.skip(Book::setOwner));
            }
        if(user.isPresent()) {
            Book book = modelMapper.map(bookDTO, Book.class);
            book.setOwner(user.get());
            return Optional.of(book);
        }
        return Optional.empty();
    }
}
