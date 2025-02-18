package io.github.enkarin.bookcrossing.utils.controller;

import io.github.enkarin.bookcrossing.constant.ErrorMessage;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UtilityControllerTest extends BookCrossingBaseTests {

    @Test
    void getErrors() {
        final List<Map<String, String>> errors = webClient.get()
            .uri("/utils/errors")
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBodyList(new ParameterizedTypeReference<Map<String, String>>() {
            })
            .returnResult().getResponseBody();

        assertThat(errors)
            .isNotNull()
            .hasSize(ErrorMessage.values().length);
    }
}
