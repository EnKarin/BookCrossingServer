package io.github.enkarin.bookcrossing.user.dto;

import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.stream.Collectors;

@Immutable
@SuperBuilder
@EqualsAndHashCode
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Данные пользователя для общего доступа")
public class UserPublicProfileDto {

    @Schema(description = "Идентификатор", example = "0")
    private final String userId;

    @Schema(description = "Имя", example = "Alex")
    private final String name;

    @Schema(description = "Город", example = "Новосибирск")
    private final String city;

    @Schema(description = "Время последнего входа", example = "2022-11-03T23:15:09.61")
    private final String loginDate;

    @Schema(description = "Книги пользователя")
    private final Set<BookModelDto> books;

    public static UserPublicProfileDto fromUser(final User user, final int zone) {
        final Set<BookModelDto> books = user.getBooks().stream()
                .map(BookModelDto::fromBook)
                .collect(Collectors.toUnmodifiableSet());
        return new UserPublicProfileDto(Integer.toString(user.getUserId()), user.getName(), user.getCity(),
                user.getLoginDate() == 0 ? "0" :
                        LocalDateTime.ofEpochSecond(user.getLoginDate(), 0, ZoneOffset.ofHours(zone))
                                .toString(),
                books);
    }
}
