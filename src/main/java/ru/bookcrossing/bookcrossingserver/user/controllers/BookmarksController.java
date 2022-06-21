package ru.bookcrossing.bookcrossingserver.user.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.bookcrossingserver.user.service.BookmarksService;

import java.security.Principal;

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
            @ApiResponse(responseCode = "404", description = "Книга с заданным Id не найдена"),
            @ApiResponse(responseCode = "200", description = "Книга добавлена")
    }
    )
    @PostMapping
    public ResponseEntity<?> saveBookmarks(@RequestParam @Parameter(description = "Идентификатор книги")
                                               final int bookId,
                                           final Principal principal){
        if(bookmarksService.saveBookmarks(bookId, principal.getName())){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Удаление из закладок",
            description = "Позволяет удалить книгу из закладок"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Книга с заданным Id не найдена"),
            @ApiResponse(responseCode = "200", description = "Книга удалена")
    }
    )
    @DeleteMapping
    public ResponseEntity<?> deleteBookmarks(@RequestParam @Parameter(description = "Идентификатор книги")
                                                 final int bookId,
                                           final Principal principal){
        if(bookmarksService.deleteBookmarks(bookId, principal.getName())){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Получение списка закладок",
            description = "Позволяет получить все закладки пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает список закладок")
    }
    )
    @GetMapping
    public ResponseEntity<?> getAll(final Principal principal){
        return new ResponseEntity<>(bookmarksService.getAll(principal.getName()), HttpStatus.OK);
    }
}
