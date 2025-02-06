package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.GenreDto;
import io.github.enkarin.bookcrossing.books.service.GenreService;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genre")
@Tag(name = "Жанры", description = "Предоставление информации о существующих в системе жанрах")
public class GenreController {
    private final GenreService genreService;

    @Operation(summary = "Получение справочника жанров", description = "Возвращает список существующих в системе жанрах на всех поддерживаемых языках")
    @ApiResponse(responseCode = "200", description = "Список жанров",
        content = {@Content(mediaType = Constant.MEDIA_TYPE, array = @ArraySchema(schema = @Schema(implementation = GenreDto.class)))})
    @GetMapping
    public GenreDto[] findAllGenre() {
        return genreService.findAllGenre();
    }
}
