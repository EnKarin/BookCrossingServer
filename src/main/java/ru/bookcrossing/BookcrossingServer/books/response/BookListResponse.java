package ru.bookcrossing.BookcrossingServer.books.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.bookcrossing.BookcrossingServer.books.model.Book;

import java.util.List;

@Data
public class BookListResponse {

    @Schema(description = "Список книг пользователя")
    private List<Book> bookList;
}
