package ru.bookcrossing.BookcrossingServer.user.controllers;

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
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;
import ru.bookcrossing.BookcrossingServer.exception.UserNotFoundException;
import ru.bookcrossing.BookcrossingServer.user.dto.UserDtoResponse;
import ru.bookcrossing.BookcrossingServer.user.dto.UserListResponse;
import ru.bookcrossing.BookcrossingServer.user.dto.UserPutRequest;
import ru.bookcrossing.BookcrossingServer.user.model.User;
import ru.bookcrossing.BookcrossingServer.user.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Objects;
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
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Возвращает профиль пользователя",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDtoResponse.class))})
    }
    )
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam @Parameter(description = "Идентификатор пользователя") int id,
                                        @RequestParam @Parameter(description = "Часовой пояс пользователя") int zone,
                                        Principal principal) {
        UserDtoResponse userDTOResponse;
        Optional<User> user;
        if (id == -1) {
            user = userService.findByLogin(principal.getName());
            userDTOResponse = new UserDtoResponse(user.orElseThrow(), zone);
        } else {
            user = userService.findById(id);
            if (user.isPresent()) {
                userDTOResponse = new UserDtoResponse(user.get(), zone);
            } else {
                ErrorListResponse response = new ErrorListResponse();
                response.getErrors().add("user: Пользователь не найден");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }
        return ResponseEntity.ok(userDTOResponse);
    }

    @Operation(
            summary = "Получение своего профиля",
            description = "Возвращает данные профиля пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает данные пользователя",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDtoResponse.class))})
    }
    )
    @GetMapping("/myProfile")
    public ResponseEntity<?> getMyProfile(Principal principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getName()));
    }

    @Operation(
            summary = "Изменение данных пользователя",
            description = "Возвращает обновленный профиль"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Пароли не совпадают",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Введен неверный старый пароль",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Некорректный пароль",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Возвращает обновленный профиль пользователя",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDtoResponse.class))})
    }
    )
    @PutMapping("/profile/edit")
    public ResponseEntity<?> putProfile(@Valid @RequestBody UserPutRequest userPutRequest,
                                        BindingResult bindingResult,
                                        Principal principal) {
        ErrorListResponse response = new ErrorListResponse();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(f -> response.getErrors()
                    .add(Objects.requireNonNull(f.getDefaultMessage())));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!userPutRequest.getNewPassword().equals(userPutRequest.getPasswordConfirm())) {
            response.getErrors().add("passwordConfirm: Пароли не совпадают");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        try {
            Optional<User> user = userService.putUserInfo(userPutRequest, principal.getName());
            if (user.isPresent()) {
                return ResponseEntity.ok(new UserDtoResponse(user.get(), userPutRequest.getZone()));
            } else {
                response.getErrors().add("oldPassword: Старый пароль неверен");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        }catch (UserNotFoundException e){
            response.getErrors().add("user: Пользователь не найден");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProfile(@RequestParam int zone){
        return new ResponseEntity<>(new UserListResponse(userService.findAllUsers(zone)), HttpStatus.OK);
    }
}

