package io.github.enkarin.bookcrossing.registration.controllers;

import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.constant.ErrorMessage;
import io.github.enkarin.bookcrossing.exception.AccountNotConfirmedException;
import io.github.enkarin.bookcrossing.exception.BindingErrorsException;
import io.github.enkarin.bookcrossing.exception.EmailFailedException;
import io.github.enkarin.bookcrossing.exception.InvalidPasswordException;
import io.github.enkarin.bookcrossing.exception.LockedAccountException;
import io.github.enkarin.bookcrossing.exception.LoginFailedException;
import io.github.enkarin.bookcrossing.exception.PasswordsDontMatchException;
import io.github.enkarin.bookcrossing.exception.TokenNotFoundException;
import io.github.enkarin.bookcrossing.registration.dto.AuthResponse;
import io.github.enkarin.bookcrossing.registration.dto.LoginRequest;
import io.github.enkarin.bookcrossing.registration.dto.UserRegistrationDto;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import io.github.enkarin.bookcrossing.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static io.github.enkarin.bookcrossing.utils.CookieConfigurator.configureRefreshTokenCookie;
import static io.github.enkarin.bookcrossing.utils.Util.createErrorMap;

@Tag(
    name = "Регистрация и авторизация пользователей",
    description = "Позволяет регистрироваться новым пользователям и выдает токен при аутентификации"
)
@RestController
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;

    @Operation(
        summary = "Регистрация пользователя",
        description = "Возвращает токены, если пользователь успешно зарегистрирован"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "409", description = "Пароли не совпадают",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))}),
        @ApiResponse(responseCode = "409", description = "Пользователь с таким логином или почтой уже существует",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))}),
        @ApiResponse(responseCode = "406", description = "Введены некорректные данные",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/ValidationErrorBody"))}),
        @ApiResponse(responseCode = "201", description = "Отправляет ссылку для подтверждения на почту",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(implementation = UserDto.class))})
    })
    @PostMapping("/registration")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody final UserRegistrationDto userForm,
                                                final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final List<String> response = new LinkedList<>();
            bindingResult.getAllErrors().forEach(f -> response.add(f.getDefaultMessage()));
            throw new BindingErrorsException(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(userForm));
    }

    @Operation(
        summary = "Авторизация пользователя",
        description = "Возвращает токены"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Некорректный логин или пароль",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))}),
        @ApiResponse(responseCode = "403", description = "Аккаунт не подтвержден или заблокирован",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Возвращает токены",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(implementation = AuthResponse.class))}, headers = @Header(name = "Set-Cookie", description = "refresh token")
        )}
    )
    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> auth(@RequestBody final LoginRequest request) {
        final AuthResponse auth = userService.findByLoginAndPassword(request);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, configureRefreshTokenCookie(auth.getRefreshToken()))
            .body(auth);
    }

    @Operation(
        summary = "Подтвержение почты",
        description = "Изменяет стату аккаунта на подтвержденный"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Токена не существует",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Подтверждает почту для аккаунта и возвращает токены",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(implementation = AuthResponse.class))}, headers = @Header(name = "Set-Cookie", description = "refresh token"))
    })
    @GetMapping("/registration/confirmation")
    public ResponseEntity<AuthResponse> mailConfirm(@RequestParam final String token) {
        final AuthResponse auth = userService.confirmMail(token);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, configureRefreshTokenCookie(auth.getRefreshToken()))
            .body(auth);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(PasswordsDontMatchException.class)
    public Map<String, String> passwordConflict() {
        return createErrorMap(ErrorMessage.ERROR_1000);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(LoginFailedException.class)
    public Map<String, String> loginFailed() {
        return createErrorMap(ErrorMessage.ERROR_1002);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmailFailedException.class)
    public Map<String, String> emailFailed() {
        return createErrorMap(ErrorMessage.ERROR_1006);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TokenNotFoundException.class)
    public Map<String, String> tokenInvalid() {
        return createErrorMap(ErrorMessage.ERROR_2004);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(LockedAccountException.class)
    public Map<String, String> lockedUser() {
        return createErrorMap(ErrorMessage.ERROR_1001);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccountNotConfirmedException.class)
    public Map<String, String> notConfirmedUser() {
        return createErrorMap(ErrorMessage.ERROR_1005);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(InvalidPasswordException.class)
    public Map<String, String> passwordInvalid() {
        return createErrorMap(ErrorMessage.ERROR_1007);
    }
}
