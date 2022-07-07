package io.github.enkarin.bookcrossing.user.controllers;

import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.errors.ErrorListResponse;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.user.dto.UserDtoResponse;
import io.github.enkarin.bookcrossing.user.dto.UserPutRequest;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import java.security.Principal;
import java.util.Optional;

@Tag(
        name = "Получение профиля пользователей",
        description = "Позволяет получить профили пользователей или редактировать свой"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserProfileController {

    private final UserService userService;

    @Operation(
            summary = "Получение чужого профиля",
            description = "Возвращает данные профиля по id"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Пользователь не найден",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = ErrorListResponse.class))}),
        @ApiResponse(responseCode = "200", description = "Возвращает профиль пользователя",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = UserDtoResponse.class))})
        }
    )
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam @Parameter(description = "Идентификатор пользователя")
                                            final int userId,
                                        @RequestParam @Parameter(description = "Часовой пояс пользователя")
                                        final int zone,
                                        final Principal principal) {
        UserDtoResponse userDTOResponse;
        User user;
        if (userId == -1) {
            user = userService.findByLogin(principal.getName());
        } else {
            user = userService.findById(userId);
        }
        userDTOResponse = new UserDtoResponse(user, zone);
        return ResponseEntity.ok(userDTOResponse);
    }

    @Operation(
            summary = "Получение своего профиля",
            description = "Возвращает данные профиля пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращает данные пользователя",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = UserDtoResponse.class))})
        }
    )
    @GetMapping("/myProfile")
    public ResponseEntity<?> getMyProfile(final Principal principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getName()));
    }

    @Operation(
            summary = "Изменение данных пользователя",
            description = "Возвращает обновленный профиль"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "409", description = "Пароли не совпадают",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorListResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorListResponse.class))}),
        @ApiResponse(responseCode = "403", description = "Введен неверный старый пароль",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorListResponse.class))}),
        @ApiResponse(responseCode = "400", description = "Некорректный пароль",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorListResponse.class))}),
        @ApiResponse(responseCode = "200", description = "Возвращает обновленный профиль пользователя",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = UserDtoResponse.class))})
        }
    )
    @PutMapping("/profile")
    public ResponseEntity<?> putProfile(@Valid @RequestBody final UserPutRequest userPutRequest,
                                        final BindingResult bindingResult,
                                        final Principal principal) {
        final ErrorListResponse response = new ErrorListResponse();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(f -> response.getErrors()
                    .add(f.getDefaultMessage()));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!userPutRequest.getNewPassword().equals(userPutRequest.getPasswordConfirm())) {
            response.getErrors().add("passwordConfirm: Пароли не совпадают");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        try {
            final Optional<User> user = userService.putUserInfo(userPutRequest, principal.getName());
            if (user.isPresent()) {
                return ResponseEntity.ok(new UserDtoResponse(user.get(), userPutRequest.getZone()));
            } else {
                response.getErrors().add("oldPassword: Старый пароль неверен");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        } catch (UserNotFoundException e) {
            response.getErrors().add("user: Пользователь не найден");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //добавить документацию

    @GetMapping("/users")
    public ResponseEntity<?> getAllProfile(@RequestParam final int zone) {
        return new ResponseEntity<>(userService.findAllUsers(zone), HttpStatus.OK);
    }
}

