package ru.bookcrossing.bookcrossingserver.books.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.bookcrossingserver.books.dto.BookDto;
import ru.bookcrossing.bookcrossingserver.books.dto.BookListResponse;
import ru.bookcrossing.bookcrossingserver.books.dto.BookResponse;
import ru.bookcrossing.bookcrossingserver.books.service.BookService;
import ru.bookcrossing.bookcrossingserver.constant.Constant;
import ru.bookcrossing.bookcrossingserver.errors.ErrorListResponse;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

@Tag(
        name = "Раздел работы с книгами",
        description = "Позволяет пользователю управлять своими книгами"
)

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/myBook")
public class MyBookController {

    private final BookService bookService;

    @Operation(
            summary = "Добавление книги",
            description = "Позволяет сохранить книгу для обмена"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Введены некорректные данные",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Возвращает сохраненную книгу",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = BookResponse.class))})}
    )
    @PostMapping
    public ResponseEntity<?> saveBook(@Valid @RequestBody final BookDto bookDTO,
                                      final BindingResult bindingResult,
                                      final Principal principal) {
        final ErrorListResponse response = new ErrorListResponse();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(f -> response.getErrors()
                    .add(Objects.requireNonNull(f.getDefaultMessage())));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        final Optional<BookResponse> book = bookService.saveBook(bookDTO, principal.getName());
        if(book.isEmpty()){
            response.getErrors().add("user: Пользователь не найден");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(book);
    }

    @Operation(
            summary = "Список книг",
            description = "Позволяет получить список всех книг пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает список книг",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = BookListResponse.class))})}
    )
    @GetMapping("/all")
    public ResponseEntity<?> bookList(final Principal principal) {
        final BookListResponse response = new BookListResponse();
        response.setBookList(bookService.findBookForOwner(principal.getName()));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Удаление книги",
            description = "Позволяет удалить книгу по ее id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Удаляет книгу из бд")}
    )
    @DeleteMapping
    public ResponseEntity<?> deleteBook(@RequestParam final int bookId){
        bookService.deleteBook(bookId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
