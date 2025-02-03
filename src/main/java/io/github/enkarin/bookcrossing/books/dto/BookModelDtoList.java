package io.github.enkarin.bookcrossing.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Список возвращаемых книг")
public record BookModelDtoList(@Schema(description = "Список возвращаемых книг") List<BookModelDto> bookDtoList) {
}
