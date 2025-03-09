package io.github.enkarin.bookcrossing.user.controllers;

import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.constant.ErrorMessage;
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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static io.github.enkarin.bookcrossing.utils.Util.createErrorMap;

@Tag(
    name = "Получение профиля пользователей",
    description = "Позволяет получить профили пользователей или редактировать свой"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/profile")
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
                schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Возвращает профиль пользователя")
    }
    )
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = USER_ID, schema = @Schema(defaultValue = "-1"), description = "Идентификатор пользователя"),
        @Parameter(in = ParameterIn.QUERY, name = "zone", description = "Часовой пояс пользователя")
    })
    @GetMapping
    public ResponseEntity<?> getProfile(@RequestHeader(name = USER_ID, defaultValue = "-1") final String userId, // NOSONAR
                                        @RequestParam final int zone,
                                        final Principal principal) {
        if ("-1".equals(userId)) {
            return ResponseEntity.ok(userService.getProfile(principal.getName()));
        }
        return ResponseEntity.ok(userService.findById(Integer.parseInt(userId), zone));
    }

    @Operation(summary = "Изменение данных пользователя", description = "Возвращает обновленный профиль")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "412", description = "Пароли не совпадают",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))}),
        @ApiResponse(responseCode = "409", description = "Неверный пароль",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))}),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))}),
        @ApiResponse(responseCode = "406", description = "Обязательные поля запроса не были заполнены",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/ValidationErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Возвращает обновленный профиль пользователя",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(implementation = UserProfileDto.class))})
    })
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
            content = {@Content(mediaType = Constant.MEDIA_TYPE, array = @ArraySchema(schema = @Schema(implementation = UserPublicProfileDto.class)))})
    })
    @GetMapping("/users")
    public ResponseEntity<List<UserPublicProfileDto>> getAllProfile(@RequestParam final int zone, @RequestParam final int pageNumber, @RequestParam final int pageSize) {
        return ResponseEntity.ok(userService.findAllUsers(zone, pageNumber, pageSize));
    }

    @Operation(summary = "Установка аватара", description = "Позволяет добавить или изменить аватар пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Аватар сохранён"),
        @ApiResponse(responseCode = "400", description = "Некорректный файл или имя файла",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))})
    })
    @PostMapping("/avatar")
    public ResponseEntity<Void> putAvatar(@ModelAttribute final MultipartFile avatar, final Principal principal) throws IOException {
        final String fileName = avatar.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new BadRequestException(ErrorMessage.ERROR_3001.getCode());
        }
        final String expansion = fileName.substring(fileName.indexOf('.') + 1).toLowerCase(Locale.ROOT);
        if ("jpeg".equals(expansion) || "jpg".equals(expansion) || "png".equals(expansion) || "bmp".equals(expansion)) {
            userService.putAvatar(principal.getName(), avatar);
        } else {
            throw new BadRequestException(ErrorMessage.ERROR_3002.getCode());
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Получение аватара", description = "Позволяет получить аватар указанного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращение аватара", content = {@Content(mediaType = Constant.JPG)}),
        @ApiResponse(responseCode = "404", description = "Указанного пользователя не существует",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/LogicErrorBody"))})
    })
    @GetMapping("/avatar")
    public void findAvatar(@RequestParam final int userId, final HttpServletResponse response) throws IOException {
        final byte[] avatar = userService.getAvatar(userId);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + userId + ".jpg\"");
        response.setContentLength(avatar.length);
        FileCopyUtils.copy(new ByteArrayInputStream(avatar), response.getOutputStream());
    }

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(PasswordsDontMatchException.class)
    public Map<String, String> passwordExc() {
        return createErrorMap(ErrorMessage.ERROR_1000);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(InvalidPasswordException.class)
    public Map<String, String> passwordInvalid(final InvalidPasswordException exc) {
        return createErrorMap(ErrorMessage.ERROR_1007);
    }
}

