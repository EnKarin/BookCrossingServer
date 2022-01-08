package ru.bookcrossing.BookcrossingServer.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ErrorListResponse {

    @Schema(description = "Список ошибок", example = "passwordConfirm: Пароли не совпадают")
    private final List<String> errors = new LinkedList<>();
}
