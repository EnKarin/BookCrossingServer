package io.github.enkarin.bookcrossing.support;

import io.github.enkarin.bookcrossing.books.dto.BookDto;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.chat.dto.MessageDto;
import io.github.enkarin.bookcrossing.chat.dto.MessagePutRequest;
import io.github.enkarin.bookcrossing.chat.dto.MessageRequest;
import io.github.enkarin.bookcrossing.chat.dto.UsersCorrKeyDto;
import io.github.enkarin.bookcrossing.registration.dto.LoginRequest;
import io.github.enkarin.bookcrossing.registration.dto.UserRegistrationDto;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import io.github.enkarin.bookcrossing.user.dto.UserParentDto;
import io.github.enkarin.bookcrossing.user.dto.UserPasswordDto;
import io.github.enkarin.bookcrossing.user.dto.UserProfileDto;
import io.github.enkarin.bookcrossing.user.dto.UserPublicProfileDto;
import io.github.enkarin.bookcrossing.user.dto.UserPutProfileDto;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
@UtilityClass
public class TestDataProvider {

    @Nonnull
    public static List<UserRegistrationDto> buildUsers() {
        return List.of(buildBot(), buildAlex(), buildMax());
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
                .bookDto(buildDorian())
                .build();
    }

    @Nonnull
    public static BookModelDto buildDandelion(final int bookId) {
        return prepareBookModel()
                .bookId(bookId)
                .bookDto(buildDandelion())
                .build();
    }

    @Nonnull
    public static BookModelDto buildWolves(final int bookId) {
        return prepareBookModel()
                .bookId(bookId)
                .bookDto(buildWolves())
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

    @Nonnull
    public static LoginRequest buildAuthAlex() {
        return prepareLogin()
                .login("Alex")
                .build();
    }

    @Nonnull
    public static LoginRequest buildAuthAdmin() {
        return prepareLogin()
                .login("admin")
                .build();
    }

    @Nonnull
    public static UserPasswordDto buildUserPasswordDto() {
        return UserPasswordDto.builder()
                .password("1234567")
                .passwordConfirm("1234567")
                .build();
    }

    @Nonnull
    public static UserPasswordDto buildInvalidUserPasswordDto() {
        return UserPasswordDto.builder()
                .password("1234")
                .passwordConfirm("1234567")
                .build();
    }

    @Nonnull
    public static UserPublicProfileDto buildPublicProfileBot(final int userId) {
        return UserPublicProfileDto.builder()
                .userId(userId)
                .name("Tester")
                .city("Novosibirsk")
                .loginDate("0")
                .books(Set.of())
                .build();
    }

    @Nonnull
    public static UserProfileDto buildProfileBot(final int userId) {
        return UserProfileDto.builder()
                .userId(userId)
                .login("Bot")
                .email("k.test@mail.ru")
                .name("Tester")
                .city("Novosibirsk")
                .build();
    }

    @Nonnull
    public static UserPutProfileDto.UserPutProfileDtoBuilder<?, ?> preparePutProfile() {
        return UserPutProfileDto.builder()
                .name("Bott")
                .city("Moscow")
                .oldPassword("123456")
                .newPassword("123456789")
                .passwordConfirm("123456789");
    }

    @Nonnull
    public static UserProfileDto buildPutProfileBot(final int userId) {
        return UserProfileDto.builder()
                .userId(userId)
                .login("Bot")
                .email("k.test@mail.ru")
                .name("Bott")
                .city("Moscow")
                .books(Set.of())
                .build();
    }

    @Nonnull
    public static MessageDto buildMessageDto(final int userId, final long messageId, final String date) {
        return MessageDto.builder()
                .messageId(messageId)
                .sender(userId)
                .declaim(false)
                .text("Hi")
                .departureDate(date)
                .build();
    }

    @Nonnull
    public static MessageRequest buildMessageRequest(final UsersCorrKeyDto key) {
        return MessageRequest.builder()
                .usersCorrKeyDto(key)
                .text("Hi")
                .build();
    }

    @Nonnull
    public static MessagePutRequest buildMessagePutRequest(final long messageId) {
        return MessagePutRequest.builder()
                .messageId(messageId)
                .text("New")
                .build();
    }

    @Nonnull
    public static UserDto buildUserDto() {
        return UserDto.builder()
                .loginDate(999_999L)
                .userParentDto(
                        UserParentDto.builder()
                                .name("UserName")
                                .login("login")
                                .build()
                )
                .build();
    }
}
