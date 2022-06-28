package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.BookFiltersRequest;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.books.service.BookService;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                    schema = @Schema(implementation = BookModelDto[].class))})
    })
    @GetMapping("/all")
    public ResponseEntity<?> books() {
        return ResponseEntity.ok(bookService.findAll().toArray());
    }

    @Operation(
            summary = "Страница книги",
            description = "Позволяет получить данные выбранной книги"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Книга не найдена",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Возвращает данные книги",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = BookModelDto.class))})
    })
    @GetMapping("/info")
    public ResponseEntity<?> bookInfo(@RequestParam final int bookId) {
        final BookModelDto book = bookService.findById(bookId);
        return ResponseEntity.ok(book);
    }

    @Operation(
            summary = "Поиск книг по названию",
            description = "Позволяет найти книги по названию"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращает найденные книги",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = BookModelDto[].class))})
    })
    @GetMapping("/searchByTitle")
    public ResponseEntity<?> searchByTitle(@RequestParam final String title) {
        return ResponseEntity.ok(bookService.findByTitle(title).toArray());
    }

    @Operation(
            summary = "Поиск книг с фильтрацией",
            description = "Позволяет найти книги с помощью фильтров"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращает найденные книги",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = BookModelDto[].class))})
    })
    @GetMapping("/searchWithFilters")
    public ResponseEntity<?> searchWithFilters(@RequestBody final BookFiltersRequest filters) {
        return ResponseEntity.ok(bookService.filter(filters).toArray());
    }
}
