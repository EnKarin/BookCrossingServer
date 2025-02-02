package io.github.enkarin.bookcrossing.user.controllers;

import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.exception.BindingErrorsException;
import io.github.enkarin.bookcrossing.exception.InvalidPasswordException;
import io.github.enkarin.bookcrossing.exception.PasswordsDontMatchException;
import io.github.enkarin.bookcrossing.user.dto.UserProfileDto;
import io.github.enkarin.bookcrossing.user.dto.UserPublicProfileDto;
import io.github.enkarin.bookcrossing.user.dto.UserPutProfileDto;
import io.github.enkarin.bookcrossing.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Tag(
        name = "Получение профиля пользователей",
        description = "Позволяет получить профили пользователей или редактировать свой"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/profile")
@Validated
public class UserProfileController {

    private static final String USER_ID = "userId";
    private final UserService userService;

    @Operation(
            summary = "Получение профиля",
            description = "Возвращает данные профиля по id"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Пользователь не найден",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Возвращает профиль пользователя")
        }
    )
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = USER_ID, schema = @Schema(defaultValue = "-1"), description = "Идентификатор пользователя"),
        @Parameter(in = ParameterIn.QUERY, name = "zone", description = "Часовой пояс пользователя")
    })
    @GetMapping
    public ResponseEntity<?> getProfile(@RequestHeader(name = USER_ID, defaultValue = "-1") @NotBlank final String userId, // NOSONAR
                                        @RequestParam final int zone,
                                        final Principal principal) {
        if ("-1".equals(userId)) {
            return ResponseEntity.ok(userService.getProfile(principal.getName()));
        }
        return ResponseEntity.ok(userService.findById(Integer.parseInt(userId), zone));
    }

    @Operation(
            summary = "Изменение данных пользователя",
            description = "Возвращает обновленный профиль"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "412", description = "Пароли не совпадают",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "409", description = "Неверный пароль",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Возвращает обновленный профиль пользователя",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = UserProfileDto.class))})
        }
    )
    @PutMapping
    public ResponseEntity<UserProfileDto> putProfile(@Valid @RequestBody final UserPutProfileDto userPutProfileDto,
                                                     final BindingResult bindingResult,
                                                     final Principal principal) {
        if (bindingResult.hasErrors()) {
            final List<String> response = new LinkedList<>();
            bindingResult.getAllErrors().forEach(f -> response.add(f.getDefaultMessage()));
            throw new BindingErrorsException(response);
        }
        return ResponseEntity.ok(userService.putUserInfo(userPutProfileDto, principal.getName()));
    }

    @Operation(
            summary = "Список пользователей",
            description = "Позволяет найти всех пользователей сервиса"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращает найденных пользователей",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = UserPublicProfileDto[].class))})
    })
    @GetMapping("/users")
    public ResponseEntity<Object[]> getAllProfile(@RequestParam final int zone) {
        return ResponseEntity.ok(userService.findAllUsers(zone).toArray());
    }

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(PasswordsDontMatchException.class)
    public Map<String, String> passwordExc(final PasswordsDontMatchException exc) {
        return Map.of("password", exc.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(InvalidPasswordException.class)
    public Map<String, String> passwordInvalid(final InvalidPasswordException exc) {
        return Map.of("password", exc.getMessage());
    }
}

