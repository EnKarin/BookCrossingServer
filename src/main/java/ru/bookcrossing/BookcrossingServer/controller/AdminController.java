package ru.bookcrossing.BookcrossingServer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.BookcrossingServer.model.response.UserDTOResponse;
import ru.bookcrossing.BookcrossingServer.model.response.UserResponse;
import ru.bookcrossing.BookcrossingServer.service.UserService;

import java.util.stream.Collectors;


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
                            schema = @Schema(implementation = UserResponse.class))})
    }
    )
    @GetMapping("/getAll")
    public ResponseEntity<?> userList() {
        UserResponse response = new UserResponse();
        response.setUserList(userService.findAllUsers().stream()
                .map(UserDTOResponse::new)
                .collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Удаление пользователя",
            description = "Позволяет удалить пользователя по его Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает на стартовую страницу")
    }
    )
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable @Parameter(description = "Идентификатор пользователя") Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("redirect:/");
    }
}
