package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.GenreDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/genre")
@Tag(name = "Жанры", description = "Предоставление информации о существующих в системе жанрах")
public class GenreController {

    @GetMapping
    public GenreDto[] findAllGenre() {
        return null;
    }
}
