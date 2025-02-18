package io.github.enkarin.bookcrossing.utils.controller;

import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.constant.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Tag(
    name = "Утилитные методы"
)
@RestController
@RequestMapping("/utils")
public class UtilityController {

    @Operation(
        summary = "Сопоставление кода к описанию ошибки"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращает список ошибок с описанием",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class)))})
    })
    @GetMapping("/errors")
    public ResponseEntity<List<ErrorMessage>> getErrors() {
        return ResponseEntity.ok(Arrays.stream(ErrorMessage.values()).toList());
    }
}
