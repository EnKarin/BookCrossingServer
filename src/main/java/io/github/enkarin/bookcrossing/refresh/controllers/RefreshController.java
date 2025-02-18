package io.github.enkarin.bookcrossing.refresh.controllers;

import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.constant.ErrorMessage;
import io.github.enkarin.bookcrossing.exception.RefreshTokenInvalidException;
import io.github.enkarin.bookcrossing.exception.TokenNotFoundException;
import io.github.enkarin.bookcrossing.refresh.service.RefreshService;
import io.github.enkarin.bookcrossing.registration.dto.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static io.github.enkarin.bookcrossing.utils.CookieConfigurator.configureRefreshTokenCookie;
import static io.github.enkarin.bookcrossing.utils.Util.createErrorMap;

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
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(implementation = AuthResponse.class))}, headers = @Header(name = "Set-Cookie", description = "refresh token")
        )}
    )
    @Parameters(
        @Parameter(name = "refresh-token", in = ParameterIn.COOKIE)
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@CookieValue(name = "refresh-token") final String token) {
        final AuthResponse auth = refreshService.updateTokens(token);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, configureRefreshTokenCookie(auth.getRefreshToken()))
            .body(auth);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TokenNotFoundException.class)
    public Map<String, String> tokenNotFound() {
        return createErrorMap(ErrorMessage.ERROR_2004);
    }

    @ResponseStatus(HttpStatus.GONE)
    @ExceptionHandler(RefreshTokenInvalidException.class)
    public Map<String, String> tokenInvalid() {
        return createErrorMap(ErrorMessage.ERROR_2002);
    }
}
