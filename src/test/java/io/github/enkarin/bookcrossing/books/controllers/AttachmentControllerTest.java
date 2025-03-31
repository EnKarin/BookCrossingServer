package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.AttachmentMultipartDto;
import io.github.enkarin.bookcrossing.books.model.Attachment;
import io.github.enkarin.bookcrossing.books.repository.AttachmentRepository;
import io.github.enkarin.bookcrossing.books.service.AttachmentService;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class AttachmentControllerTest extends BookCrossingBaseTests {
    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private AttachmentService attachmentService;
    private UserDto user;

    @BeforeEach
    void initUser() {
        user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
    }

    @Test
    @SneakyThrows
    void saveAttachment() {
        final var booksId = createAndSaveBooks(user.getLogin());
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ByteArrayResource(Files.readAllBytes(file.toPath())), MediaType.TEXT_PLAIN).filename(file.getName());
        multipartBodyBuilder.part("bookId", booksId.get(0));

        webClient.post()
            .uri(uriBuilder -> uriBuilder.pathSegment("user", "myBook", "attachment").build())
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .bodyValue(multipartBodyBuilder.build())
            .exchange()
            .expectStatus().isEqualTo(201);

        assertThat(attachmentRepository.count()).isOne();
    }

    @Test
    @SneakyThrows
    void findAttachment() {
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), user.getLogin()).getBookId();
        final int attachmentId = createAttachment(book1);

        final var attachment = webClient.get()
            .uri(uriBuilder -> uriBuilder.pathSegment("user", "myBook", "attachment")
                .queryParam("id", attachmentId)
                .queryParam("format", "ORIGIN").build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody().returnResult().getResponseBody();

        assertThat(attachment).isNotNull();
    }

    @Test
    @SneakyThrows
    void deleteAttachment() {
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), user.getLogin()).getBookId();
        final int attachmentId = createAttachment(book1);

        webClient.delete()
            .uri(uriBuilder -> uriBuilder.pathSegment("user", "myBook", "attachment")
                .queryParam("id", attachmentId)
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .exchange()
            .expectStatus().isEqualTo(200);

        assertThat(attachmentRepository.count()).isZero();
    }

    @Test
    @SneakyThrows
    void deleteAttachmentFromOtherUser() {
        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), user.getLogin()).getBookId();
        final int attachmentId = createAttachment(book1);
        final UserDto alex = createAndSaveUser(TestDataProvider.buildAlex());
        enabledUser(alex.getUserId());

        webClient.delete()
            .uri(uriBuilder -> uriBuilder.pathSegment("user", "myBook", "attachment")
                .queryParam("id", attachmentId)
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthAlex())))
            .exchange()
            .expectStatus().isEqualTo(403);

        assertThat(attachmentRepository.findAll()).extracting(Attachment::getAttachId).containsOnly(attachmentId);
    }

    private int createAttachment(final int bookId) throws IOException {
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/jpg", Files.readAllBytes(file.toPath()));
        return attachmentService.saveTitleAttachment(AttachmentMultipartDto.fromFile(bookId, multipartFile), user.getLogin()).getTitleAttachmentId();
    }
}
