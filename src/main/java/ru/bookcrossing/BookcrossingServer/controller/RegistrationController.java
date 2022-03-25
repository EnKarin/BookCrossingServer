package ru.bookcrossing.BookcrossingServer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.bookcrossing.BookcrossingServer.config.jwt.JwtProvider;
import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.model.DTO.UserDTO;
import ru.bookcrossing.BookcrossingServer.model.request.LoginRequest;
import ru.bookcrossing.BookcrossingServer.model.request.RefreshRequest;
import ru.bookcrossing.BookcrossingServer.model.response.AuthResponse;
import ru.bookcrossing.BookcrossingServer.model.response.ErrorListResponse;
import ru.bookcrossing.BookcrossingServer.service.RefreshService;
import ru.bookcrossing.BookcrossingServer.service.UserService;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

@Tag(
        name = "Регистрация и аутентификация пользователей",
        description = "Позволяет регистрироваться новым пользователям и выдает токен при аутентификации"
)
@RestController
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final RefreshService refreshService;

    @Operation(
            summary = "Регистрация пользователя",
            description = "Возвращает токены, если пользователь успешно зарегистрирован"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Пароли не совпадают",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "406", description = "Пользователь с таким логином уже существует",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Введены некорректные данные",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Возвращает токены",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))})
    }
    )
    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userForm, BindingResult bindingResult) {
        ErrorListResponse errorListResponse = new ErrorListResponse();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(f -> errorListResponse.getErrors()
                    .add(Objects.requireNonNull(f.getDefaultMessage())));
            return new ResponseEntity<>(errorListResponse, HttpStatus.BAD_REQUEST);
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            errorListResponse.getErrors().add("passwordConfirm: Пароли не совпадают");
            return new ResponseEntity<>(errorListResponse, HttpStatus.CONFLICT);
        }
        if (!userService.saveUser(userForm)) {
            errorListResponse.getErrors().add("login: Пользователь с таким логином уже существует");
            return new ResponseEntity<>(errorListResponse, HttpStatus.NOT_ACCEPTABLE);
        }

        AuthResponse response = new AuthResponse();
        response.setAccessToken(jwtProvider.generateToken(userForm.getLogin()));
        response.setRefreshToken(refreshService.createToken(userForm.getLogin()));

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Выдает токены, если пользователь с таким логином существует и пароль корректен"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Некорректные данные",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Возвращает токены",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))}
            )}
    )
    @PostMapping("/auth")
    public ResponseEntity<?> auth(@RequestBody LoginRequest request) {
        Optional<User> userEntity = userService.findByLoginAndPassword(request);
        if (userEntity.isEmpty()) {
            ErrorListResponse response = new ErrorListResponse();
            response.getErrors().add("account: Некорректный логин или пароль");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(jwtProvider.generateToken(request.getLogin()));
        authResponse.setRefreshToken(refreshService.createToken(request.getLogin()));

        return ResponseEntity.ok(authResponse);
    }

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
