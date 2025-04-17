package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.AttachmentMultipartDto;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.books.enums.FormatType;
import io.github.enkarin.bookcrossing.books.exceptions.NoAccessToAttachmentException;
import io.github.enkarin.bookcrossing.constant.ErrorMessage;
import io.github.enkarin.bookcrossing.exception.AttachmentNotFoundException;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.exception.UnsupportedImageTypeException;
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
    void saveTitleAttachmentShouldWork(final String fileName, final String contentType) throws IOException {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final BookModelDto book1 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());
        final File file = ResourceUtils.getFile(fileName);
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), contentType, Files.readAllBytes(file.toPath()));

        attachmentService.saveTitleAttachment(AttachmentMultipartDto.fromFile(book1.getBookId(), multipartFile), users.get(1).getLogin());

        assertThat(bookService.findById(book1.getBookId()).getTitleAttachmentId()).isNotNull();
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
    void saveTitleAttachmentShouldWork() throws IOException {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final BookModelDto book1 =  bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/jpg", Files.readAllBytes(file.toPath()));
        attachmentService.saveTitleAttachment(AttachmentMultipartDto.fromFile(book1.getBookId(), multipartFile), users.get(1).getLogin());
        final File secondFile = ResourceUtils.getFile("classpath:files/nature.jpeg");
        final MultipartFile secondMultipartFile =
            new MockMultipartFile(secondFile.getName(), secondFile.getName(), "image/jpg", Files.readAllBytes(secondFile.toPath()));

        attachmentService.saveTitleAttachment(AttachmentMultipartDto.fromFile(book1.getBookId(), secondMultipartFile), users.get(1).getLogin());

        final BookModelDto targetBook = bookService.findById(book1.getBookId());
        assertThat(targetBook.getTitleAttachmentId()).isNotNull();
        assertThat(attachmentService.findAttachmentData(targetBook.getTitleAttachmentId(), FormatType.ORIGIN).getData()).isEqualTo(secondMultipartFile.getBytes());
    }

    @Test
    void saveTitleAttachmentShouldFailWithFileFormat() throws IOException {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        final File file = ResourceUtils.getFile("classpath:files/text.txt");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "text/plain", Files.readAllBytes(file.toPath()));
        final AttachmentMultipartDto dto = AttachmentMultipartDto.fromFile(book1, multipartFile);
        final var userLogin = users.get(1).getLogin();
        assertThatThrownBy(() -> attachmentService.saveTitleAttachment(dto, userLogin))
            .isInstanceOf(UnsupportedImageTypeException.class)
            .hasMessage(ErrorMessage.ERROR_3002.getCode());
    }

    @Test
    void saveTitleAttachmentShouldFailWithoutBook() throws IOException {
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/jpg", Files.readAllBytes(file.toPath()));
        final AttachmentMultipartDto dto = AttachmentMultipartDto.fromFile(Integer.MAX_VALUE, multipartFile);
        final var userLogin = users.get(1).getLogin();
        assertThatThrownBy(() -> attachmentService.saveTitleAttachment(dto, userLogin))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessage("Книга не найдена");
    }

    @Test
    void saveTitleAttachmentShouldFailWithFileWithoutName() throws IOException {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());

        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), Files.readAllBytes(file.toPath()));
        final AttachmentMultipartDto dto = AttachmentMultipartDto.fromFile(book1, multipartFile);
        final var userLogin = users.get(1).getLogin();
        assertThatThrownBy(() -> attachmentService.saveTitleAttachment(dto, userLogin))
            .isInstanceOf(UnsupportedImageTypeException.class)
            .hasMessage(ErrorMessage.ERROR_3001.getCode());
    }

    @Test
    void deleteAttachmentShouldWork() throws IOException {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/jpg", Files.readAllBytes(file.toPath()));
        attachmentService.saveTitleAttachment(AttachmentMultipartDto.fromFile(book1, multipartFile), users.get(1).getLogin());
        final int titleAttachmentId = bookService.findById(book1).getTitleAttachmentId();

        attachmentService.deleteAttachment(titleAttachmentId, users.get(1).getLogin());

        assertThat(jdbcTemplate.queryForObject("select exists(select * from bookcrossing.t_attach where attach_id = ?)", Boolean.class, titleAttachmentId)).isFalse();
        assertThat(bookService.findAll(0, 5)).hasSize(4);
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
            .isInstanceOf(AttachmentNotFoundException.class)
            .hasMessage("Вложение не найдено");
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
        attachmentService.saveTitleAttachment(AttachmentMultipartDto.fromFile(book1, multipartFile), users.get(1).getLogin());

        assertThat(attachmentService.findAttachmentData(bookService.findById(book1).getTitleAttachmentId(), FormatType.ORIGIN).getData()).isEqualTo(multipartFile.getBytes());
    }

    @Test
    @SneakyThrows
    void saveAdditionalAttachmentAfterTitleAttachment() {
        final BookModelDto book1 =  bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin());
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/jpg", Files.readAllBytes(file.toPath()));
        attachmentService.saveTitleAttachment(AttachmentMultipartDto.fromFile(book1.getBookId(), multipartFile), users.get(1).getLogin());
        final File secondFile = ResourceUtils.getFile("classpath:files/nature.jpeg");
        final MultipartFile secondMultipartFile =
            new MockMultipartFile(secondFile.getName(), secondFile.getName(), "nature/jpeg", Files.readAllBytes(secondFile.toPath()));

        attachmentService.saveAdditionalAttachment(AttachmentMultipartDto.fromFile(book1.getBookId(), secondMultipartFile), users.get(1).getLogin());

        assertThat(bookService.findById(book1.getBookId())).satisfies(bookModelDto -> {
                assertThat(bookModelDto.getTitleAttachmentId()).isNotNull();
                assertThat(bookModelDto.getAdditionalAttachmentIdList()).hasSize(1).doesNotContain(bookModelDto.getTitleAttachmentId());
            });
    }

    @Test
    @SneakyThrows
    void saveAdditionalAttachmentWithoutTitleAttachment() {
        final BookModelDto book1 =  bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin());
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/jpg", Files.readAllBytes(file.toPath()));

        attachmentService.saveAdditionalAttachment(AttachmentMultipartDto.fromFile(book1.getBookId(), multipartFile), users.get(1).getLogin());

        final BookModelDto bookWithAdditionalAttachment = bookService.findById(book1.getBookId());
            assertThat(bookWithAdditionalAttachment).satisfies(bookModelDto -> {
                assertThat(bookModelDto.getAdditionalAttachmentIdList()).isEmpty();
                assertThat(bookModelDto.getTitleAttachmentId()).isNotNull();
            });
        assertThat(bookService.findById(book1.getBookId()).getTitleAttachmentId()).isEqualTo(bookWithAdditionalAttachment.getTitleAttachmentId());
    }

    @Test
    @SneakyThrows
    void findListImageAttachment() {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/jpg", Files.readAllBytes(file.toPath()));
        attachmentService.saveTitleAttachment(AttachmentMultipartDto.fromFile(book1, multipartFile), users.get(1).getLogin());

        assertThat(attachmentService.findAttachmentData(bookService.findById(book1).getTitleAttachmentId(), FormatType.LIST).getData().length)
            .isLessThan(multipartFile.getBytes().length);
    }

    @Test
    @SneakyThrows
    void findThumbImageAttachment() {
        final int attachmentId = bookService.findById(createAttachment()).getTitleAttachmentId();

        assertThat(attachmentService.findAttachmentData(attachmentId, FormatType.THUMB).getData().length)
            .isLessThan(attachmentService.findAttachmentData(attachmentId, FormatType.LIST).getData().length);
    }

    @Test
    @SneakyThrows
    void throwDeleteAttachmentFromOtherUser() {
        final int attachmentId = bookService.findById(createAttachment()).getTitleAttachmentId();

        assertThatThrownBy(() -> attachmentService.deleteAttachment(attachmentId, TestDataProvider.buildMax().getLogin())).isInstanceOf(NoAccessToAttachmentException.class);
    }

    private int createAttachment() throws IOException {
        bookService.saveBook(TestDataProvider.buildDandelion(), users.get(0).getLogin());
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), users.get(1).getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(1).getLogin());
        bookService.saveBook(TestDataProvider.buildDorian(), users.get(0).getLogin());
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/jpg", Files.readAllBytes(file.toPath()));
        attachmentService.saveTitleAttachment(AttachmentMultipartDto.fromFile(book1, multipartFile), users.get(1).getLogin());
        return book1;
    }

    @Test
    void findNotExistsAttachmentMustThrowException() {
        assertThatThrownBy(() -> attachmentService.findAttachmentData(10, FormatType.ORIGIN)).isInstanceOf(AttachmentNotFoundException.class);
    }
}
