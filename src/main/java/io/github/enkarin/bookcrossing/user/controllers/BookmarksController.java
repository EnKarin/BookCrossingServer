package io.github.enkarin.bookcrossing.user.controllers;

import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.user.service.BookmarksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Tag(
        name = "Работа с закладками",
        description = "Позволяет добавлять, удалять и просматривать закладки"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/bookmarks")
public class BookmarksController {

    private final BookmarksService bookmarksService;

    @Operation(
            summary = "Добавление в закладки",
            description = "Позволяет сохранить книгу в закладки"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Книга с заданным Id не найдена",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "201", description = "Возвращает список закладок",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = BookModelDto[].class))})
        }
    )
    @PostMapping
    public ResponseEntity<List<BookModelDto>> saveBookmarks(@RequestParam @Parameter(description = "Идентификатор книги") final int bookId, final Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookmarksService.saveBookmarks(bookId, principal.getName()));
    }

    @Operation(
            summary = "Удаление из закладок",
            description = "Позволяет удалить книгу из закладок"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Книга с заданным Id не найдена",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Книга удалена")
        }
    )
    @DeleteMapping
    public ResponseEntity<Void> deleteBookmarks(@RequestParam @Parameter(description = "Идентификатор книги")
                                                 final int bookId,
                                           final Principal principal) {
        bookmarksService.deleteBookmarks(bookId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Получение списка закладок",
            description = "Позволяет получить все закладки пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращает список закладок",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = BookModelDto[].class))})
        }
    )
    @GetMapping
    public ResponseEntity<List<BookModelDto>> getAll(final Principal principal) {
        return ResponseEntity.ok(bookmarksService.getAll(principal.getName()));
    }
}
