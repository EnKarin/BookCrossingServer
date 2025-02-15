package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.repository.AttachmentRepository;
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
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class AttachmentControllerTest extends BookCrossingBaseTests {
    @Autowired
    private AttachmentRepository attachmentRepository;
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
    void findAttachment() {
    }

    @Test
    void deleteAttachment() {
    }
}