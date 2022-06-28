package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

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
        @ApiResponse(responseCode = "201", description = "Возвращает сохраненную книгу",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = BookModelDto[].class))})}
    )
    @PostMapping
    public ResponseEntity<?> saveBook(@Valid @RequestBody final BookDto bookDTO,
                                      final BindingResult bindingResult,
                                      final Principal principal) {
        final ErrorListResponse response = new ErrorListResponse();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(f -> response.getErrors()
                    .add(Objects.requireNonNull(f.getDefaultMessage())));
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookService.saveBook(bookDTO, principal.getName()));
    }

    @Operation(
            summary = "Список книг",
            description = "Позволяет получить список всех книг пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращает список книг",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = BookModelDto[].class))})}
    )
    @GetMapping("/all")
    public ResponseEntity<?> bookList(final Principal principal) {
        final List<BookModelDto> bookModelDtos = bookService.findBookForOwner(principal.getName());
        return ResponseEntity.ok(bookModelDtos.toArray());
    }

    @Operation(
            summary = "Удаление книги",
            description = "Позволяет удалить книгу по ее id"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Книга не найдена",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Удаляет книгу из бд")}
    )
    @DeleteMapping
    public ResponseEntity<?> deleteBook(@RequestParam final int bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
