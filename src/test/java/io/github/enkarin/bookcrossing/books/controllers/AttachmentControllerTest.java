package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.AttachmentMultipartDto;
import io.github.enkarin.bookcrossing.books.service.AttachmentService;
import io.github.enkarin.bookcrossing.registration.dto.UserRegistrationDto;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class AttachmentControllerTest extends BookCrossingBaseTests {

    @Autowired
    private AttachmentService attachmentService;

    @Test
    void deleteAttachmentShouldWork() throws IOException {
        final UserRegistrationDto userRegistrationDto = TestDataProvider.buildBot();
        final int user = createAndSaveUser(userRegistrationDto).getUserId();
        enabledUser(user);

        final int book1 = bookService.saveBook(TestDataProvider.buildWolves(), userRegistrationDto.getLogin()).getBookId();
        bookService.saveBook(TestDataProvider.buildDorian(), userRegistrationDto.getLogin());

        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                "image/jpg", Files.readAllBytes(file.toPath()));
        final int name = attachmentService.saveAttachment(AttachmentMultipartDto.fromFile(book1, multipartFile),
                userRegistrationDto.getLogin()).getAttachment().getAttachId();

        webClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/user/myBook/attachment")
                        .queryParam("bookId", book1)
                        .build())
                .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
                .exchange()
                .expectStatus().isOk();

        assertThat(jdbcTemplate.queryForObject("select exists(select * from bookcrossing.t_attach where attach_id = ?)",
                Boolean.class, name))
                .isFalse();
        assertThat(bookService.findAll())
                .hasSize(2);
    }
}
