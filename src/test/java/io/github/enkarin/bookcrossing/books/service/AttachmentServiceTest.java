package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.books.dto.AttachmentDto;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.exception.AttachmentNotFoundException;
import io.github.enkarin.bookcrossing.exception.BadRequestException;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttachmentServiceTest extends BookCrossingBaseTests {

    @Autowired
    private BookService bookService;

    @Autowired
    private AttachmentService attachmentService;

    private List<UserDto> users;

    @BeforeEach
    void create() {
        users = TestDataProvider.buildUsers().stream()
                .map(u -> userService.saveUser(u))
                .collect(Collectors.toList());
        users.forEach(u -> usersId.add(u.getUserId()));
    }

    @Test
    void saveAttachment() throws IOException {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final BookModelDto book1 =  bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        final File file = ResourceUtils.getFile("classpath:image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                "image/jpg", Files.readAllBytes(file.toPath()));
        assertThat(attachmentService.saveAttachment(AttachmentDto.fromFile(book1.getBookId(), multipartFile),
                users.get(1).getLogin()).getAttachment().getAttachId())
                .isEqualTo(book1.getBookId());
    }

    @Test
    void saveTxtAttachmentException() throws IOException {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 =  bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        final File file = ResourceUtils.getFile("classpath:text.txt");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                "text/plain", Files.readAllBytes(file.toPath()));
        assertThatThrownBy(() -> attachmentService.saveAttachment(AttachmentDto.fromFile(book1, multipartFile),
                users.get(1).getLogin()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Недопустимый формат файла");
    }

    @Test
    void saveAttachmentWithoutBookException() throws IOException {
        final File file = ResourceUtils.getFile("classpath:image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                "image/jpg", Files.readAllBytes(file.toPath()));
        assertThatThrownBy(() -> attachmentService.saveAttachment(AttachmentDto
                        .fromFile(Integer.MAX_VALUE, multipartFile),
                users.get(1).getLogin()))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Книга не найдена");
    }

    @Test
    void saveAttachmentWithoutNameException() throws IOException {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 =  bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        final File file = ResourceUtils.getFile("classpath:image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), Files.readAllBytes(file.toPath()));
        assertThatThrownBy(() -> attachmentService.saveAttachment(AttachmentDto
                        .fromFile(book1, multipartFile),
                users.get(1).getLogin()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Имя не должно быть пустым");
    }

    @Test
    void deleteAttachment() throws IOException {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 =  bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        final File file = ResourceUtils.getFile("classpath:image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                "image/jpg", Files.readAllBytes(file.toPath()));
        final int name = attachmentService.saveAttachment(AttachmentDto.fromFile(book1, multipartFile),
                users.get(1).getLogin()).getAttachment().getAttachId();
        attachmentService.deleteAttachment(book1, users.get(1).getLogin());
        assertThat(jdbcTemplate.queryForObject("select exists(select * from t_attach where attach_id = ?)",
                Boolean.class, name))
                .isFalse();
        assertThat(bookService.findAll())
                .hasSize(3);
    }

    @Test
    void deleteWithoutAttachmentException() {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 =  bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        assertThatThrownBy(() -> attachmentService.deleteAttachment(book1, users.get(1).getLogin()))
                .isInstanceOf(AttachmentNotFoundException.class)
                .hasMessage("Вложение не найдено");
    }

    @Test
    void deleteAttachmentWithoutBookException() {
        assertThatThrownBy(() -> attachmentService.deleteAttachment(Integer.MAX_VALUE, users.get(1).getLogin()))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Книга не найдена");
    }
}
