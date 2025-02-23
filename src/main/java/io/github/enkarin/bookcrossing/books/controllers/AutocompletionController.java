package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.service.AutocompletionService;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;

@Tag(name = "Автодополнение", description = "Позволяет получить существующие в сервисе наименования по подстроке")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/books/autocompletion")
public class AutocompletionController {
    private final AutocompletionService autocompletionService;

    @Operation(summary = "Автодополнение названия книги и имён авторов", description = "Позволяет получить существующие в системе названия книг и авторов по части названия")
    @ApiResponse(responseCode = "200", description = "Имеющиеся в системе названия книг и авторов",
        content = @Content(mediaType = Constant.MEDIA_TYPE, array = @ArraySchema(schema = @Schema(implementation = String.class))))
    @GetMapping
    public List<String> bookNameOrAuthorAutocompletion(@RequestParam final String name) {
        return autocompletionService.autocompleteBookNameOrAuthor(name);
    }

    @Operation(summary = "Поиск авторов в выборке", description = "Позволяет выполнять внепагинационный поиск авторов по названию книги или имени автора")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Найденные по запросу авторы",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
        @ApiResponse(responseCode = "400", description = "Поле name не должно быть пустым",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/NewErrorBody"))})})
    @GetMapping("/authors")
    public ResponseEntity<Collection<String>> findAuthors(
        @RequestParam @NotBlank(message = "3008") @Parameter(description = "Частичное называние книги или имя автора") final String name) {
        return ResponseEntity.ok(autocompletionService.findAuthorNamesByTitleOrAuthor(name));
    }
}
