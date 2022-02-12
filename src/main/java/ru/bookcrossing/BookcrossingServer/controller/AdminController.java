package ru.bookcrossing.BookcrossingServer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.BookcrossingServer.model.response.AdmUserListResponse;
import ru.bookcrossing.BookcrossingServer.service.UserService;

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
            summary = "Удаление пользователя",
            description = "Позволяет удалить пользователя по его логину"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает на стартовую страницу")
    }
    )
    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String login) {
        userService.deleteUser(login);
        return ResponseEntity.ok("redirect:/");
    }
}
