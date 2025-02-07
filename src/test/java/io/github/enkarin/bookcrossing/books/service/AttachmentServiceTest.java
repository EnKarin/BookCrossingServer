package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.AttachmentMultipartDto;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.books.exceptions.UnsupportedFormatException;
import io.github.enkarin.bookcrossing.exception.AttachmentNotFoundException;
import io.github.enkarin.bookcrossing.exception.BadRequestException;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttachmentServiceTest extends BookCrossingBaseTests {
    @Autowired
    private AttachmentService attachmentService;

    private List<UserDto> users;

    @BeforeEach
    void create() {
        users = TestDataProvider.buildUsers().stream()
            .map(this::createAndSaveUser)
            .toList();
    }

    @ParameterizedTest
    @MethodSource("provideFile")
    void saveAttachmentShouldWork(final String fileName, final String contentType) throws IOException {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final BookModelDto book1 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        final File file = ResourceUtils.getFile(fileName);
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), contentType, Files.readAllBytes(file.toPath()));
        assertThat(attachmentService.saveAttachment(AttachmentMultipartDto.fromFile(book1.getBookId(), multipartFile), users.get(1).getLogin()).getAttachmentId())
            .isEqualTo(book1.getBookId());
    }

    static Stream<Arguments> provideFile() {
        return Stream.of(
            Arguments.of("classpath:files/image.jpg", "image/jpg"),
            Arguments.of("classpath:files/black.bmp", "image/bmp"),
            Arguments.of("classpath:files/nature.jpeg", "image/jpeg"),
            Arguments.of("classpath:files/antelope.png", "image/png")
        );
    }

    @Test
    void saveAttachmentShouldFailWithFileFormat() throws IOException {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        final File file = ResourceUtils.getFile("classpath:files/text.txt");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "text/plain", Files.readAllBytes(file.toPath()));
        final AttachmentMultipartDto dto = AttachmentMultipartDto.fromFile(book1, multipartFile);
        final var userLogin = users.get(1).getLogin();
        assertThatThrownBy(() -> attachmentService.saveAttachment(dto, userLogin))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Недопустимый формат файла");
    }

    @Test
    void saveAttachmentShouldFailWithoutBook() throws IOException {
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
            "image/jpg", Files.readAllBytes(file.toPath()));
        final AttachmentMultipartDto dto = AttachmentMultipartDto.fromFile(Integer.MAX_VALUE, multipartFile);
        final var userLogin = users.get(1).getLogin();
        assertThatThrownBy(() -> attachmentService.saveAttachment(dto, userLogin))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessage("Книга не найдена");
    }

    @Test
    void saveAttachmentShouldFailWithFileWithoutName() throws IOException {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), Files.readAllBytes(file.toPath()));
        final AttachmentMultipartDto dto = AttachmentMultipartDto.fromFile(book1, multipartFile);
        final var userLogin = users.get(1).getLogin();
        assertThatThrownBy(() -> attachmentService.saveAttachment(dto, userLogin))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Имя не должно быть пустым");
    }

    @Test
    void deleteAttachmentShouldWork() throws IOException {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/jpg", Files.readAllBytes(file.toPath()));
        final int name = attachmentService.saveAttachment(AttachmentMultipartDto.fromFile(book1, multipartFile), users.get(1).getLogin()).getAttachmentId();

        attachmentService.deleteAttachment(book1, users.get(1).getLogin());

        assertThat(jdbcTemplate.queryForObject("select exists(select * from bookcrossing.t_attach where attach_id = ?)", Boolean.class, name)).isFalse();
        assertThat(bookService.findAll()).hasSize(4);
    }

    @Test
    void deleteAttachmentShouldFailWithoutAttach() {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());
        final var userLogin = users.get(1).getLogin();

        assertThatThrownBy(() -> attachmentService.deleteAttachment(book1, userLogin))
            .isInstanceOf(AttachmentNotFoundException.class)
            .hasMessage("Вложение не найдено");
    }

    @Test
    void deleteAttachmentShouldFailWithoutBook() {
        final var userLogin = users.get(1).getLogin();
        assertThatThrownBy(() -> attachmentService.deleteAttachment(Integer.MAX_VALUE, userLogin))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessage("Книга не найдена");
    }

    @Test
    @SneakyThrows
    void findAttachment() {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/jpg", Files.readAllBytes(file.toPath()));
        final int attachmentId = attachmentService.saveAttachment(AttachmentMultipartDto.fromFile(book1, multipartFile), users.get(1).getLogin()).getAttachmentId();

        assertThat(attachmentService.findAttachmentData(attachmentId, "origin")).isEqualTo(multipartFile.getBytes());
    }

    @Test
    @SneakyThrows
    void findAttachmentWithUnexpectedFormatMustThrowException() {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/jpg", Files.readAllBytes(file.toPath()));
        final int attachmentId = attachmentService.saveAttachment(AttachmentMultipartDto.fromFile(book1, multipartFile), users.get(1).getLogin()).getAttachmentId();

        assertThatThrownBy(() -> attachmentService.findAttachmentData(attachmentId, "aboba")).isInstanceOf(UnsupportedFormatException.class);
    }

    @Test
    void findNotExistsAttachmentMustThrowException() {
        assertThatThrownBy(() -> attachmentService.findAttachmentData(10, "origin")).isInstanceOf(AttachmentNotFoundException.class);
    }
}
