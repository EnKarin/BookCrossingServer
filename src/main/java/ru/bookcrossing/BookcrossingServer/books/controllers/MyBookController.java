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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.BookcrossingServer.books.dto.BookDto;
import ru.bookcrossing.BookcrossingServer.books.response.BookListResponse;
import ru.bookcrossing.BookcrossingServer.books.response.BookResponse;
import ru.bookcrossing.BookcrossingServer.books.service.BookService;
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Objects;

@Tag(
        name = "Раздел работы с книгами",
        description = "Позволяет пользователю управлять своими книгами"
)

@RestController
@RequestMapping("/user/myBook")
public class MyBookController {

    private BookService bookService;

    @Autowired
    private void setBookService(BookService s) {
        bookService = s;
    }

    @Operation(
            summary = "Добавление книги",
            description = "Позволяет сохранить книгу для обмена"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Введены некорректные данные",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Возвращает сохраненную книгу",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookResponse.class))})}
    )
    @PostMapping("/save")
    public ResponseEntity<?> saveBook(@Valid @RequestBody BookDto bookDTO,
                                      BindingResult bindingResult,
                                      Principal principal) {
        if (bindingResult.hasErrors()) {
            ErrorListResponse response = new ErrorListResponse();
            bindingResult.getAllErrors().forEach(f -> response.getErrors()
                    .add(Objects.requireNonNull(f.getDefaultMessage())));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(bookService.saveBook(bookDTO, principal.getName()));
    }

    @Operation(
            summary = "Список книг",
            description = "Позволяет получить список всех книг пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает список книг",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookListResponse.class))})}
    )
    @GetMapping("/getAll")
    public ResponseEntity<?> bookList(Principal principal) {
        BookListResponse response = new BookListResponse();
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
    @PostMapping("/delete")
    public ResponseEntity<?> deleteBook(@RequestParam Integer id){
        bookService.deleteBook(id);
        return ResponseEntity.ok("redirect:/");
    }
}
