package io.github.enkarin.bookcrossing.registation.controllers;

import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.errors.ErrorListResponse;
import io.github.enkarin.bookcrossing.exception.EmailFailedException;
import io.github.enkarin.bookcrossing.exception.LoginFailedException;
import io.github.enkarin.bookcrossing.mail.service.MailService;
import io.github.enkarin.bookcrossing.refresh.service.RefreshService;
import io.github.enkarin.bookcrossing.registation.dto.AuthResponse;
import io.github.enkarin.bookcrossing.registation.dto.LoginRequest;
import io.github.enkarin.bookcrossing.registation.dto.UserDto;
import io.github.enkarin.bookcrossing.security.jwt.JwtProvider;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.service.UserService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

@Tag(
        name = "Регистрация и авторизация пользователей",
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
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = ErrorListResponse.class))}),
        @ApiResponse(responseCode = "406", description = "Пользователь с таким логином уже существует",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = ErrorListResponse.class))}),
        @ApiResponse(responseCode = "400", description = "Введены некорректные данные",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = ErrorListResponse.class))}),
        @ApiResponse(responseCode = "200", description = "Возвращает токены",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = AuthResponse.class))})
        }
    )
    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@Valid @RequestBody final UserDto userForm,
                                          final BindingResult bindingResult) {
        final ErrorListResponse errorListResponse = new ErrorListResponse();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(f -> errorListResponse.getErrors()
                    .add(Objects.requireNonNull(f.getDefaultMessage())));
            return new ResponseEntity<>(errorListResponse, HttpStatus.BAD_REQUEST);
        }
        userService.saveUser(userForm);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Авторизация пользователя",
            description = "Выдает токены, если пользователь с таким логином существует и пароль корректен"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Некорректные данные",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = ErrorListResponse.class))}),
        @ApiResponse(responseCode = "200", description = "Возвращает токены",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = AuthResponse.class))}
            )}
    )
    @PostMapping("/auth")
    public ResponseEntity<?> auth(@RequestBody final LoginRequest request) {
        final User userEntity = userService.findByLoginAndPassword(request);
        final ErrorListResponse response = new ErrorListResponse();
        //TODO: после rebase изменить
        if (userEntity.isEnabled()) {
            if (userEntity.isAccountNonLocked()) {
                return ResponseEntity.ok(refreshService.createTokens(request.getLogin()));
            } else {
                response.getErrors().add("account: Аккаунт заблокирован");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } else {
            response.getErrors().add("account: Аккаунт не подтвержден");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(
            summary = "Подтвержение почты",
            description = "Изменяет стату аккаунта на подтвержденный"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Токена не существует"),
        @ApiResponse(responseCode = "200", description = "Подтверждает почту для аккаунта")
        }
    )
    @GetMapping("/registration/confirmation")
    public ResponseEntity<?> mailConfirm(@RequestParam final String token) {
        final Optional<String> login = userService.confirmMail(token);
        if (login.isPresent()) {
            return ResponseEntity.ok(refreshService.createTokens(login.get()));
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
