package ru.bookcrossing.BookcrossingServer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    @GetMapping("/getAll")
    public ResponseEntity<?> userList() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(
            summary = "Удаление пользователя",
            description = "Позволяет удалить пользователя по его Id"
    )
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable @Parameter(description = "Идентификатор пользователя") Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("redirect:/");
    }
}
