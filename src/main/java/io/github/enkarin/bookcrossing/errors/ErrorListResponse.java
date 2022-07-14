package io.github.enkarin.bookcrossing.errors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class ErrorListResponse {

    @Schema(description = "Список ошибок", example = "источник: причина")
    private final List<String> errors = new LinkedList<>();
}
