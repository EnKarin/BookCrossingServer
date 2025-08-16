package io.github.enkarin.bookcrossing.user.controllers;

import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.exception.PasswordsDontMatchException;
import io.github.enkarin.bookcrossing.exception.TokenInvalidException;
import io.github.enkarin.bookcrossing.mail.service.MailService;
import io.github.enkarin.bookcrossing.user.dto.UserPasswordDto;
import io.github.enkarin.bookcrossing.user.service.ResetPasswordService;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Map;

@Tag(
        name = "Сброс пароля",
        description = "Позволяет получить на привязанную почту ссылку для сброса пароля"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/reset")
public class PasswordResetController {

    private final MailService mailService;
    private final ResetPasswordService resetPasswordService;

    @Operation(
            summary = "Запрос на сброс пароля",
            description = "Отправляет на почту ссылку для сброса пароля"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Пользователь с таким email не найден",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Отправляет ссылку на сброс пароля на почту")
        }
    )
    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestParam final String email) {
        mailService.sendResetPassword(email);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Смена пароля",
            description = "Смена пароля по ссылке"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "409", description = "Пароли не совпадают",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "403", description = "Ссылка недействительна",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "201", description = "Пароль успешно изменен")
        }
    )
    @PostMapping("/update")
    public ResponseEntity<Void> updatePassword(@RequestParam final String token,
                                            @Valid @RequestBody final UserPasswordDto userPasswordDto,
                                            final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new PasswordsDontMatchException();
        }
        resetPasswordService.updatePassword(token, userPasswordDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(PasswordsDontMatchException.class)
    public Map<String, String> passwordConflict(final PasswordsDontMatchException exc) {
        return Map.of("password", exc.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(TokenInvalidException.class)
    public Map<String, String> tokenInvalid(final TokenInvalidException exc) {
        return Map.of("token", exc.getMessage());
    }
}
