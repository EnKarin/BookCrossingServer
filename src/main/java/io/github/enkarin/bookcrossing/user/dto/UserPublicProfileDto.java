package io.github.enkarin.bookcrossing.user.dto;

import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

@Getter
@Schema(description = "Данные пользователя для общего доступа")
public class UserPublicProfileDto {

    @Schema(description = "Идентификатор", example = "0")
    private final int userId;

    @Schema(description = "Имя", example = "Alex")
    private final String name;

    @Schema(description = "Город", example = "Новосибирск")
    private final String city;

    @Schema(description = "Время последнего входа", example = "2022-11-03T23:15:09.61")
    private String loginDate;

    @Schema(description = "Книги пользователя")
    private final Set<Book> books;

    private UserPublicProfileDto(final int userId, final String name, final String city,
                           final long loginDate, final Set<Book> books, final int zone) {
        this.userId = userId;
        this.name = name;
        this.city = city;
        if (loginDate != 0) {
            this.loginDate = LocalDateTime.ofEpochSecond(loginDate, 0, ZoneOffset.ofHours(zone)).toString();
        }
        this.books = books;
    }

    public static UserPublicProfileDto fromUser(final User user, final int zone) {
        return new UserPublicProfileDto(user.getUserId(), user.getName(), user.getCity(),
                user.getLoginDate(), user.getBooks(), zone);
    }
}
