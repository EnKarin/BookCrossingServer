package io.github.enkarin.bookcrossing.refresh.controllers;

import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.errors.ErrorListResponse;
import io.github.enkarin.bookcrossing.refresh.dto.RefreshRequest;
import io.github.enkarin.bookcrossing.refresh.service.RefreshService;
import io.github.enkarin.bookcrossing.registation.dto.AuthResponse;
import io.github.enkarin.bookcrossing.security.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Tag(
        name = "Обновление токенов пользователя",
        description = "Позволяет обновить токены"
)
@RestController
@RequiredArgsConstructor
public class RefreshController {

    private final JwtProvider jwtProvider;
    private final RefreshService refreshService;

    @Operation(
            summary = "Обновление токенов",
            description = "Выдает токены, если refresh корректен"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "Токена не существует или истек срок его действия",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = ErrorListResponse.class))}),
        @ApiResponse(responseCode = "200", description = "Возвращает токены",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = AuthResponse.class))}
            )}
    )
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody final RefreshRequest request) {
        final Optional<String> login = refreshService.findByToken(request.getRefresh());
        if (login.isPresent()) {
            final AuthResponse authResponse = new AuthResponse();
            authResponse.setAccessToken(jwtProvider.generateToken(login.get()));
            authResponse.setRefreshToken(refreshService.createToken(login.get()));
            return ResponseEntity.ok(authResponse);
        }
        final ErrorListResponse response = new ErrorListResponse();
        response.getErrors().add("refresh: Неверный или истекший токен");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}
