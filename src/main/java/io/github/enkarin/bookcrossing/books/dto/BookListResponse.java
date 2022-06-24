package io.github.enkarin.bookcrossing.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class BookListResponse {

    @Schema(description = "Список книг пользователя")
    private List<BookModelDto> bookList;

    public BookListResponse(List<BookModelDto> bookModelDtos) {
        setBookList(bookModelDtos);
    }
}
