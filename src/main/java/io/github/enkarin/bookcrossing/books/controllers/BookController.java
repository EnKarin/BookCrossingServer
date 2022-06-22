package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookListResponse;
import io.github.enkarin.bookcrossing.books.dto.BookResponse;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.books.service.BookService;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.errors.ErrorListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(
        name = "Раздел со всеми книгами в системе",
        description = "Позволяет пользователю получить все книги, доступные для обмена"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @Operation(
            summary = "Все книги в системе",
            description = "Позволяет получить книги всех пользователей"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает все книги",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = BookListResponse.class))})
    })
    @GetMapping("/all")
    public ResponseEntity<?> books() {
        final BookListResponse bookResponse = new BookListResponse();
        bookResponse.setBookList(bookService.findAll());
        return ResponseEntity.ok(bookResponse);
    }

    @Operation(
            summary = "Страница книги",
            description = "Позволяет получить данные выбранной книги"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Книга не найдена",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Возвращает данные книги",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = BookResponse.class))})
    })
    @GetMapping("/info")
    public ResponseEntity<?> bookInfo(@RequestParam final int bookId) {
        BookResponse bookResponse;
        final Optional<Book> book = bookService.findById(bookId);
        if (book.isPresent()) {
            bookResponse = new BookResponse(book.get());
        } else {
            final ErrorListResponse response = new ErrorListResponse();
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
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = BookListResponse.class))})
    })
    @GetMapping("/searchByTitle")
    public ResponseEntity<?> searchByTitle(@RequestParam final String title) {
        final BookListResponse response = new BookListResponse();
        response.setBookList(bookService.findByTitle(title));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Поиск книг с фильтрацией",
            description = "Позволяет найти книги с помощью фильтров"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает найденные книги",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = BookListResponse.class))})
    })
    @GetMapping("/searchWithFilters")
    public ResponseEntity<?> searchWithFilters(@RequestBody final BookFiltersRequest filters) {
        final BookListResponse response = new BookListResponse();
        response.setBookList(bookService.filter(filters));
        return ResponseEntity.ok(response);
    }
}
