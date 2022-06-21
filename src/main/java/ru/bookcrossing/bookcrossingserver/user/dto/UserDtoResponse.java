package ru.bookcrossing.bookcrossingserver.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.bookcrossing.bookcrossingserver.books.model.Book;
import ru.bookcrossing.bookcrossingserver.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

@Schema(description = "Данные пользователя для общего доступа")
@Data
public class UserDtoResponse {

    @Schema(description = "Идентификатор", example = "0")
    private int userId;

    @Schema(description = "Имя", example = "Alex")
    private String name;

    @Schema(description = "Город", example = "Новосибирск")
    private String city;

    @Schema(description = "Время последнего входа", example = "2022-11-03T23:15:09.61")
    private String loginDate;

    @Schema(description = "Книги пользователя")
    private Set<Book> books;

    public UserDtoResponse(final User user, final int zone){
        userId = user.getUserId();
        name = user.getName();
        city = user.getCity();
        if(user.getLoginDate() != 0) {
            loginDate = LocalDateTime.ofEpochSecond(user.getLoginDate(), 0, ZoneOffset.ofHours(zone)).toString();
        }
        books = user.getBooks();
    }
}
