package ru.bookcrossing.BookcrossingServer.refresh.controllers;

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
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;
import ru.bookcrossing.BookcrossingServer.refresh.request.RefreshRequest;
import ru.bookcrossing.BookcrossingServer.refresh.service.RefreshService;
import ru.bookcrossing.BookcrossingServer.registation.response.AuthResponse;
import ru.bookcrossing.BookcrossingServer.security.jwt.JwtProvider;

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
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Возвращает токены",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))}
            )}
    )
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        Optional<String> login = refreshService.findByToken(request.getRefresh());
        if (login.isPresent()) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setAccessToken(jwtProvider.generateToken(login.get()));
            authResponse.setRefreshToken(refreshService.createToken(login.get()));

            return ResponseEntity.ok(authResponse);
        }
        ErrorListResponse response = new ErrorListResponse();
        response.getErrors().add("refresh: Неверный или истекший токен");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}
