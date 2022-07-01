package io.github.enkarin.bookcrossing.refresh.controllers;

import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.exception.RefreshTokenInvalidException;
import io.github.enkarin.bookcrossing.exception.TokenNotFoundException;
import io.github.enkarin.bookcrossing.refresh.dto.RefreshRequest;
import io.github.enkarin.bookcrossing.refresh.service.RefreshService;
import io.github.enkarin.bookcrossing.registation.dto.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Tag(
        name = "Обновление токенов пользователя",
        description = "Позволяет обновить токены"
)
@RestController
@RequiredArgsConstructor
public class RefreshController {

    private final RefreshService refreshService;

    @Operation(
            summary = "Обновление токенов",
            description = "Выдает токены, если refresh корректен"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "410", description = "Токен истек",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "404", description = "Токена не существует",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Возвращает токены",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = AuthResponse.class))}
            )}
    )
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody final RefreshRequest request) {
        return ResponseEntity.ok(refreshService.createTokens(request.getRefresh()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TokenNotFoundException.class)
    public Map<String, String> tokenNotFound(final TokenNotFoundException exc) {
        return Collections.singletonMap("refresh", exc.getMessage());
    }

    @ResponseStatus(HttpStatus.GONE)
    @ExceptionHandler(RefreshTokenInvalidException.class)
    public Map<String, String> tokenInvalid(final RefreshTokenInvalidException exc) {
        return Collections.singletonMap("refresh", exc.getMessage());
    }
}
