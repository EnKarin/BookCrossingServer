package ru.bookcrossing.BookcrossingServer.books.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.BookcrossingServer.books.dto.BookFiltersRequest;
import ru.bookcrossing.BookcrossingServer.books.dto.BookListResponse;
import ru.bookcrossing.BookcrossingServer.books.dto.BookResponse;
import ru.bookcrossing.BookcrossingServer.books.model.Book;
import ru.bookcrossing.BookcrossingServer.books.service.BookService;
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;

import java.util.Optional;

@Tag(
        name = "Раздел со всеми книгами в системе",
        description = "Позволяет пользователю получить все книги, доступные для обмена"
)

@RestController
@RequestMapping("/books")
public class BookController {

    private BookService bookService;

    @Autowired
    private void setBookService(BookService s) {
        bookService = s;
    }

    @Operation(
            summary = "Все книги в системе",
            description = "Позволяет получить книги всех пользователей"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает все книги",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookListResponse.class))})
    })
    @GetMapping("/getAll")
    public ResponseEntity<?> books() {
        BookListResponse bookResponse = new BookListResponse();
        bookResponse.setBookList(bookService.findAll());
        return ResponseEntity.ok(bookResponse);
    }

    @Operation(
            summary = "Страница книги",
            description = "Позволяет получить данные выбранной книги"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Книга не найдена",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Возвращает данные книги",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookResponse.class))})
    })
    @GetMapping("/getInfo")
    public ResponseEntity<?> bookInfo(@RequestParam Integer id) {
        BookResponse bookResponse;
        Optional<Book> book = bookService.findById(id);
        if (book.isPresent()) {
            bookResponse = new BookResponse(book.get());
        } else {
            ErrorListResponse response = new ErrorListResponse();
            response.getErrors().add("book: Книга не найдена");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(bookResponse);
    }

    @Operation(
            summary = "Поиск книг по названию",
            description = "Позволяет найти книги по названию"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает найденные книги",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookListResponse.class))})
    })
    @GetMapping("/searchByTitle")
    public ResponseEntity<?> searchByTitle(@RequestParam String title) {
        BookListResponse response = new BookListResponse();
        response.setBookList(bookService.findByTitle(title));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Поиск книг с фильтрацией",
            description = "Позволяет найти книги с помощью фильтров"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает найденные книги",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookListResponse.class))})
    })
    @GetMapping("/searchWithFilters")
    public ResponseEntity<?> searchWithFilters(@RequestBody BookFiltersRequest filters) {
        BookListResponse response = new BookListResponse();
        response.setBookList(bookService.filter(filters));
        return ResponseEntity.ok(response);
    }
}
