package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.books.dto.ChangeBookDto;
import io.github.enkarin.bookcrossing.books.service.BookService;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.exception.BindingErrorsException;
import io.github.enkarin.bookcrossing.exception.GenreNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static io.github.enkarin.bookcrossing.constant.ErrorMessage.ERROR_2007;
import static io.github.enkarin.bookcrossing.utils.Util.createErrorMap;
import static java.util.Objects.nonNull;

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
        @ApiResponse(responseCode = "406", description = "Введены некорректные данные",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/ValidationErrorBody"))}),
        @ApiResponse(responseCode = "201", description = "Возвращает сохраненную книгу",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(implementation = BookModelDto.class))})}
    )
    @PostMapping
    public ResponseEntity<BookModelDto> saveBook(@Valid @RequestBody final BookDto bookDTO, final BindingResult bindingResult, final Principal principal) {
        if (bindingResult.hasErrors()) {
            final List<String> response = new LinkedList<>();
            bindingResult.getAllErrors().forEach(f -> response.add(f.getDefaultMessage()));
            throw new BindingErrorsException(response);
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
            content = {@Content(mediaType = Constant.MEDIA_TYPE, array = @ArraySchema(schema = @Schema(implementation = BookModelDto.class)))})}
    )
    @GetMapping
    public ResponseEntity<List<BookModelDto>> bookList(@RequestParam final int pageNumber, @RequestParam final int pageSize, final Principal principal) {
        return ResponseEntity.ok(bookService.findBookForOwner(principal.getName(), pageNumber, pageSize));
    }

    @Operation(summary = "Редактирование книги", description = "Присваивает указанной книге текущего пользователя заполненные атрибуты запроса")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Книга с внесёнными изменениями",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, array = @ArraySchema(schema = @Schema(implementation = BookModelDto.class)))}),
        @ApiResponse(responseCode = "404", description = "Указанная книга или жанр не найдены",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))})
    })
    @PutMapping
    public ResponseEntity<BookModelDto> putBook(@RequestBody final ChangeBookDto bookDto, final Principal principal) {
        if (nonNull(bookDto.getTitle()) && !bookDto.getTitle().isBlank()) {
            bookService.changeBookTitle(principal.getName(), bookDto.getBookId(), bookDto.getTitle());
        }
        if (nonNull(bookDto.getGenre())) {
            bookService.changeBookGenre(principal.getName(), bookDto.getBookId(), bookDto.getGenre());
        }
        if (nonNull(bookDto.getAuthor()) && !bookDto.getAuthor().isBlank()) {
            bookService.changeBookAuthor(principal.getName(), bookDto.getBookId(), bookDto.getAuthor());
        }
        if (nonNull(bookDto.getPublishingHouse())) {
            bookService.changeBookPublishingHouse(principal.getName(), bookDto.getBookId(), bookDto.getPublishingHouse());
        }
        if (nonNull(bookDto.getYear())) {
            bookService.changeBookYear(principal.getName(), bookDto.getBookId(), bookDto.getYear());
        }
        if (nonNull(bookDto.getStatus())) {
            bookService.changeBookStatus(principal.getName(), bookDto.getBookId(), bookDto.getStatus());
        }
        return ResponseEntity.ok(bookService.findById(bookDto.getBookId()));
    }

    @Operation(
        summary = "Удаление книги",
        description = "Позволяет удалить книгу по ее id"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Книга не найдена",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Удаляет книгу из бд")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteBook(@RequestParam final int bookId, final Principal principal) {
        bookService.deleteBook(principal.getName(), bookId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ExceptionHandler(GenreNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> genreNotFoundExceptionHandler() {
        return createErrorMap(ERROR_2007);
    }
}
