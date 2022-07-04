package io.github.enkarin.bookcrossing.refresh.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class RefreshRequest {

    @Schema(description = "Токен обновления", example = "cac2ce3e-9ff0-49a7-8afc-3dcae34eafea", required = true)
    private final String refresh;

    private RefreshRequest(final String refresh) {
        this.refresh = refresh;
    }

    @JsonCreator
    public static RefreshRequest create(final String refresh) {
        return new RefreshRequest(refresh);
    }
}
