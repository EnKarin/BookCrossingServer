package ru.bookcrossing.BookcrossingServer.admin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.BookcrossingServer.admin.dto.AdmUserListResponse;
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;
import ru.bookcrossing.BookcrossingServer.user.service.UserService;

@Tag(
        name="Управление пользователями для администратора",
        description="Позволяет получить и удалять пользователей"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/adm")
public class AdminController {

    private final UserService userService;

    @Operation(
            summary = "Список пользователей",
            description = "Позволяет получить список пользователей"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает список пользователей",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdmUserListResponse.class))})
    }
    )
    @GetMapping("/getAll")
    public ResponseEntity<?> userList() {
        AdmUserListResponse response = new AdmUserListResponse();
        response.setUserList(userService.findAllUsers());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Блокирование пользователя",
            description = "Позволяет заблокировать пользователя по его логину"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает на стартовую страницу"),
            @ApiResponse(responseCode = "403", description = "Пользователя с таким логином не существует",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))})
    }
    )
    @PostMapping("/locked")
    public ResponseEntity<?> lockedUser(@RequestParam String login) {
        if(userService.lockedUser(login)){
            return ResponseEntity.ok("redirect:/");
        }
        else{
            ErrorListResponse response = new ErrorListResponse();
            response.getErrors().add("login: Некорректный логин пользователя");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Разблокировка пользователя",
            description = "Позволяет разблокировать пользователя по его логину"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает на стартовую страницу"),
            @ApiResponse(responseCode = "403", description = "Пользователя с таким логином не существует",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))})
    })
    @PostMapping("/nonLocked")
    public ResponseEntity<?> nonLockedUser(@RequestParam String login) {
        if(userService.nonLockedUser(login)){
            return ResponseEntity.ok("redirect:/");
        }
        else{
            ErrorListResponse response = new ErrorListResponse();
            response.getErrors().add("login: Некорректный логин пользователя");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
