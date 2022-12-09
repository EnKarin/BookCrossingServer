package io.github.enkarin.bookcrossing.refresh.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.concurrent.Immutable;

@Immutable
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Сущность для обновления токена", requiredProperties = "refresh")
public class RefreshRequest {

    @Schema(description = "Токен обновления", example = "cac2ce3e-9ff0-49a7-8afc-3dcae34eafea")
    private final String refresh;

    @JsonCreator
    public static RefreshRequest create(final String refresh) {
        return new RefreshRequest(refresh);
    }
}
