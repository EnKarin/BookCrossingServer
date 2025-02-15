package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.repository.AttachmentRepository;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;

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
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/jpg", Files.readAllBytes(file.toPath()));
        webClient.post()
            .uri(uriBuilder -> uriBuilder.pathSegment("user", "myBook", "attachment").build())
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromFormData())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .exchange()
            .expectStatus().isEqualTo(200);

        assertThat(attachmentRepository.count()).isOne();
    }

    @Test
    void findAttachment() {
    }

    @Test
    void deleteAttachment() {
    }
}