package io.github.enkarin.bookcrossing.user.controllers;

import io.github.enkarin.bookcrossing.constant.ErrorMessage;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserProfileDto;
import io.github.enkarin.bookcrossing.user.dto.UserPublicProfileDto;
import io.github.enkarin.bookcrossing.user.dto.UserPutProfileDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileControllerTest extends BookCrossingBaseTests {

    @Test
    void getMyProfileShouldWork() {
        final var user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        createAndSaveBooks(user.getLogin());

        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "profile")
                .queryParam("zone", 0)
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(UserProfileDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .isNotNull()
            .satisfies(r -> {
                assertThat(r)
                    .usingRecursiveComparison()
                    .ignoringFields("books")
                    .isEqualTo(TestDataProvider.buildProfileBot(user.getUserId()));
            });
    }

    @Test
    void getAlienProfileShouldWork() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());
        final var userAlex = createAndSaveUser(TestDataProvider.buildAlex()).getUserId();

        createAndSaveBooks(userBot.getLogin());

        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "profile")
                .queryParam("zone", 0)
                .build())
            .headers(headers -> {
                headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot()));
                headers.set("userId", String.valueOf(userAlex));
            })
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(UserPublicProfileDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .isNotNull()
            .isEqualTo(TestDataProvider.buildPublicProfileAlex(userAlex));
    }

    @Test
    void putProfileShouldWork() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        final var response = webClient.put()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "profile")
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .bodyValue(TestDataProvider.preparePutProfile().build())
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(UserProfileDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .isNotNull()
            .isEqualTo(TestDataProvider.buildPutProfileBot(userBot.getUserId()));
    }

    @Test
    void putProfileShouldFailWithPasswordDontMatch() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        assertThatReturnException(TestDataProvider.preparePutProfile().passwordConfirm("123456").build(), 412, ErrorMessage.ERROR_1000.getCode());
    }

    @Test
    void putProfileShouldFailWithPasswordInvalid() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        assertThatReturnException(TestDataProvider.preparePutProfile().oldPassword("123457").build(), 409, ErrorMessage.ERROR_1007.getCode());
    }

    @Test
    void putProfileShouldFailWithBindingErrorsException() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        final var map = webClient.put()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "profile")
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .bodyValue(TestDataProvider.preparePutProfile().newPassword("123").build())
            .exchange().expectStatus()
            .isEqualTo(406).expectBody(new ParameterizedTypeReference<Map<String, List<String>>>() {
            }).returnResult().getResponseBody();

        assertThat(map)
            .isNotEmpty()
            .extracting(m -> m.get("errorList"))
            .isEqualTo(List.of("3010"));
    }

    @Test
    void putProfileShouldWorkWhenUpdateOneField() {
        final var userBot = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(userBot.getUserId());

        final UserPutProfileDto putProfileDto = UserPutProfileDto.builder().name("Andrianov").build();
        final UserProfileDto newProfile = webClient.put()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "profile")
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .bodyValue(putProfileDto)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(UserProfileDto.class)
            .returnResult().getResponseBody();

        assertThat(newProfile)
            .isNotNull()
            .extracting(UserProfileDto::getName, UserProfileDto::getCity)
            .containsExactly(putProfileDto.getName(), userBot.getCity());
    }

    @Test
    void getAllProfileShouldWork() {
        final var user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        createAndSaveBooks(user.getLogin());

        final var response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "profile", "users")
                .queryParam("zone", 0)
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(UserPublicProfileDto.class)
            .returnResult().getResponseBody();

        assertThat(response)
            .hasSize(1)
            .singleElement()
            .satisfies(r -> assertThat(r)
                .usingRecursiveComparison()
                .ignoringFields("books", "loginDate")
                .isEqualTo(TestDataProvider.buildPublicProfileBot(user.getUserId()))
            );
    }

    @Test
    @SneakyThrows
    void putAvatar() {
        final var user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("avatar", new ByteArrayResource(Files.readAllBytes(file.toPath())), MediaType.IMAGE_JPEG).filename(file.getName());

        webClient.post()
            .uri(uriBuilder -> uriBuilder.pathSegment("user", "profile", "avatar").build())
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(multipartBodyBuilder.build())
            .headers(httpHeaders -> httpHeaders.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .exchange()
            .expectStatus().isEqualTo(201);

        assertThat(userService.getAvatar(user.getUserId())).isNotNull();
    }

    @Test
    @SneakyThrows
    void putAvatarWithNotImageFileMustThrowException() {
        final var user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        final File file = ResourceUtils.getFile("classpath:files/text.txt");
        final MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("avatar", new ByteArrayResource(Files.readAllBytes(file.toPath())), MediaType.IMAGE_JPEG).filename(file.getName());

        webClient.post()
            .uri(uriBuilder -> uriBuilder.pathSegment("user", "profile", "avatar").build())
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(multipartBodyBuilder.build())
            .headers(httpHeaders -> httpHeaders.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .exchange()
            .expectStatus().isEqualTo(415)
            .expectBody().jsonPath("$.error").isEqualTo("3002");
    }

    @Test
    @SneakyThrows
    void getAvatar() {
        final var user = createAndSaveUser(TestDataProvider.buildBot());
        enabledUser(user.getUserId());
        final File file = ResourceUtils.getFile("classpath:files/image.jpg");
        final MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/jpg", Files.readAllBytes(file.toPath()));
        userService.putAvatar(user.getLogin(), multipartFile);

        final var avatar = webClient.get()
            .uri(uriBuilder -> uriBuilder.pathSegment("user", "profile", "avatar")
                .queryParam("userId", user.getUserId())
                .build())
            .headers(httpHeaders -> httpHeaders.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody().returnResult().getResponseBody();

        assertThat(avatar).isNotNull();
    }

    private void assertThatReturnException(final UserPutProfileDto putProfileDto, final int status, final String message) {
        webClient.put()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("user", "profile")
                .build())
            .headers(headers -> headers.setBearerAuth(generateAccessToken(TestDataProvider.buildAuthBot())))
            .bodyValue(putProfileDto)
            .exchange()
            .expectStatus().isEqualTo(status)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(message);
    }
}
