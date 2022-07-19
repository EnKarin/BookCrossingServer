package io.github.enkarin.bookcrossing.support;


import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.registration.dto.LoginRequest;
import io.github.enkarin.bookcrossing.registration.dto.UserRegistrationDto;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.util.List;

@UtilityClass
public class TestDataProvider {

    @Nonnull
    public static List<UserRegistrationDto> buildUsers() {
        return List.of(buildBot(), buildAlex());
    }

    @Nonnull
    public static List<BookDto> buildBooks() {
        return List.of(buildDorian(), buildDandelion(), buildWolves());
    }

    @Nonnull
    public static List<BookModelDto> buildBookModels(final int id1, final int id2, final int id3) {
        return List.of(buildDorian(id1), buildDandelion(id2), buildWolves(id3));
    }

    @Nonnull
    public static UserRegistrationDto.UserRegistrationDtoBuilder<?, ?> prepareUser() {
        return UserRegistrationDto.builder()
                .name("Tester")
                .password("123456")
                .passwordConfirm("123456")
                .city("Novosibirsk");
    }

    @Nonnull
    public static BookDto.BookDtoBuilder<?, ?> prepareBook() {
        return BookDto.builder()
                .author("author")
                .publishingHouse("publishing_house");
    }

    @Nonnull
    public static BookModelDto.BookModelDtoBuilder<?, ?> prepareBookModel() {
        return BookModelDto.builder()
                .attachment(null);
    }

    @Nonnull
    public static LoginRequest.LoginRequestBuilder<?, ?> prepareLogin() {
        return LoginRequest.builder()
                .password("123456")
                .zone(0);
    }

    @Nonnull
    public static BookDto buildDorian() {
        return prepareBook()
                .title("Dorian")
                .genre(null)
                .year(2000)
                .build();
    }

    @Nonnull
    public static BookDto buildDandelion() {
        return prepareBook()
                .title("Dandelion")
                .genre("novel")
                .author("author2")
                .year(2020)
                .build();
    }

    @Nonnull
    public static BookDto buildWolves() {
        return prepareBook()
                .title("Wolves")
                .genre("story")
                .year(2000)
                .build();
    }

    @Nonnull
    public static BookModelDto buildDorian(final int bookId) {
        return prepareBookModel()
                .bookId(bookId)
                .bookDto(prepareBook()
                        .title("Dorian")
                        .year(2000)
                        .build())
                .build();
    }

    @Nonnull
    public static BookModelDto buildDandelion(final int bookId) {
        return prepareBookModel()
                .bookId(bookId)
                .bookDto(prepareBook()
                        .title("Dandelion")
                        .author("author2")
                        .genre("novel")
                        .year(2020)
                        .build())
                .build();
    }

    @Nonnull
    public static BookModelDto buildWolves(final int bookId) {
        return prepareBookModel()
                .bookId(bookId)
                .bookDto(prepareBook()
                        .title("Wolves")
                        .genre("story")
                        .year(2000)
                        .build())
                .build();
    }

    @Nonnull
    public static UserRegistrationDto buildBot() {
        return prepareUser()
                .login("Bot")
                .email("k.test@mail.ru")
                .build();
    }

    @Nonnull
    public static UserRegistrationDto buildAlex() {
        return prepareUser()
                .login("Alex")
                .email("t.test@mail.ru")
                .build();
    }

    @Nonnull
    public static UserRegistrationDto buildNonConfirmedPasswordUser() {
        return prepareUser()
                .passwordConfirm("654321")
                .login("User")
                .email("u.test@mail.ru")
                .build();
    }

    @Nonnull
    public static UserRegistrationDto buildMax() {
        return prepareUser()
                .login("Max")
                .email("m.test@mail.ru")
                .build();
    }

    @Nonnull
    public static UserRegistrationDto buildUserWithAlexEmail() {
        return prepareUser()
                .login("NotAlex")
                .email("t.test@mail.ru")
                .build();
    }

    @Nonnull
    public static LoginRequest buildAuthBot() {
        return prepareLogin()
                .login("Bot")
                .build();
    }
}
