package ru.bookcrossing.BookcrossingServer.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.bookcrossing.BookcrossingServer.model.DTO.BookDTO;

import java.util.List;

@Data
public class BookResponse {

    @Schema(description = "Список книг пользователя")
    private List<BookDTO> bookList;
}
