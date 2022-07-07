package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.books.dto.AttachmentDto;
import io.github.enkarin.bookcrossing.books.model.Attachment;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import org.junit.jupiter.api.AfterEach;
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

class AttachmentServiceTest extends BookCrossingBaseTests {

    @Autowired
    private BookService bookService;

    @Autowired
    private AttachmentService attachmentService;

    @AfterEach
    void tearDown() {
        usersId.forEach(u -> userService.deleteUser(u));
        usersId.clear();
    }

    @Test
    void saveAttachment() throws IOException {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
                .map(u -> userService.saveUser(u))
                .collect(Collectors.toList());
        users.forEach(u -> usersId.add(u.getUserId()));

        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 =  bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        final File file = ResourceUtils.getFile("classpath:image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                "image/jpg", Files.readAllBytes(file.toPath()));
        assertThat(attachmentService.saveAttachment(AttachmentDto.fromFile(book1, multipartFile),
                users.get(1).getLogin()).getAttachment())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("name", "book_id")
                .isEqualTo(new Attachment(null, Files.readAllBytes(file.toPath()), ".jpg", null));
    }

    @Test
    void deleteAttachment() throws IOException {
        final List<UserDto> users = TestDataProvider.buildUsers().stream()
                .map(u -> userService.saveUser(u))
                .collect(Collectors.toList());
        users.forEach(u -> usersId.add(u.getUserId()));

        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 =  bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        final File file = ResourceUtils.getFile("classpath:image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                "image/jpg", Files.readAllBytes(file.toPath()));
        final String name = attachmentService.saveAttachment(AttachmentDto.fromFile(book1, multipartFile),
                users.get(1).getLogin()).getAttachment().getName();
        attachmentService.deleteAttachment(book1, users.get(1).getLogin());
        assertThat(jdbcTemplate.queryForObject("select exists(select * from t_attach where name = ?)",
                Boolean.class, name))
                .isFalse();
    }
}
