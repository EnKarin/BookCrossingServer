package ru.bookcrossing.BookcrossingServer.controller;

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
import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.model.request.UserPutRequest;
import ru.bookcrossing.BookcrossingServer.model.response.ErrorListResponse;
import ru.bookcrossing.BookcrossingServer.model.response.UserDTOResponse;
import ru.bookcrossing.BookcrossingServer.service.UserService;

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
            summary = "Получение профиля",
            description = "Возвращает данные профиля по id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Пользователь не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Возвращает профиль пользователя",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTOResponse.class))})
    }
    )
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam @Parameter(description = "Идентификатор пользователя," +
            " -1 для собственного") int id,
                                        Principal principal) {
        UserDTOResponse userDTOResponse;
        Optional<User> user;
        if (id == -1) {
            user = userService.findByLogin(principal.getName());
            userDTOResponse = new UserDTOResponse(user.get());
        } else {
            user = userService.findById(id);
            if (user.isPresent()) {
                userDTOResponse = new UserDTOResponse(user.get());
            } else {
                ErrorListResponse response = new ErrorListResponse();
                response.getErrors().add("user: Пользователь не найден");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }
        return ResponseEntity.ok(userDTOResponse);
    }

    @Operation(
            summary = "Изменение данных пользователя",
            description = "Возвращает обновленный профиль"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Пароли не совпадают",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Введен неверный старый пароль",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Введены неверные данные",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Возвращает обновленный профиль пользователя",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTOResponse.class))})
    }
    )
    @PutMapping("/profile/edit")
    public ResponseEntity<?> putProfile(@Valid @RequestBody UserPutRequest userPutRequest,
                                        Principal principal,
                                        BindingResult bindingResult) {
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
        Optional<User> user = userService.putUserInfo(userPutRequest, principal.getName());
        if (user.isPresent()) {
            if(!userPutRequest.getNewPassword().equals(userPutRequest.getOldPassword())){
                return ResponseEntity.ok(new UserDTOResponse(user.get()));
            }
            return ResponseEntity.ok(new UserDTOResponse(user.get()));
        }
        response.getErrors().add("oldPassword: Старый пароль неверен");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}
