package io.github.enkarin.bookcrossing.registration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Уникальный логин")
public record OriginalLoginResponse(@Schema(description = "Уникальный логин") String originalLogin) {
}
