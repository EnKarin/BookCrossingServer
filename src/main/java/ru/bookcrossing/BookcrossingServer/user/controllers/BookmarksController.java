package ru.bookcrossing.BookcrossingServer.user.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.BookcrossingServer.user.service.BookmarksService;

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
    @PostMapping("/save")
    public ResponseEntity<?> saveBookmarks(@RequestParam @Parameter(description = "Идентификатор книги") int bookId,
                                           Principal principal){
        boolean result = bookmarksService.saveBookmarks(bookId, principal.getName());
        if(result){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    @PostMapping("/delete")
    public ResponseEntity<?> deleteBookmarks(@RequestParam @Parameter(description = "Идентификатор книги") int bookId,
                                           Principal principal){
        boolean result = bookmarksService.deleteBookmarks(bookId, principal.getName());
        if(result){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(
            summary = "Получение списка закладок",
            description = "Позволяет получить все закладки пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает список закладок")
    }
    )
    @GetMapping("/get")
    public ResponseEntity<?> getAll(Principal principal){
        return new ResponseEntity<>(bookmarksService.getAll(principal.getName()), HttpStatus.OK);
    }
}
