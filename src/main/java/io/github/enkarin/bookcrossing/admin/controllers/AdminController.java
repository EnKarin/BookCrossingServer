package io.github.enkarin.bookcrossing.admin.controllers;

import io.github.enkarin.bookcrossing.admin.dto.InfoUsersDto;
import io.github.enkarin.bookcrossing.admin.dto.LockedUserDto;
import io.github.enkarin.bookcrossing.admin.service.AdminService;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.errors.ErrorListResponse;
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
import java.util.Objects;

@Tag(
        name = "Управление пользователями для администратора",
        description = "Позволяет получить и удалять пользователей"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/adm")
public class AdminController {

    private final AdminService adminService;

    @Operation(
            summary = "Список пользователей",
            description = "Позволяет получить список пользователей"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращает список пользователей",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = InfoUsersDto[].class))})
        }
    )
    @GetMapping("/all")
    public ResponseEntity<?> userList(@RequestParam @Parameter(description = "Часовой пояс") final int zone) {
        return ResponseEntity.ok(adminService.findAllUsers(zone).toArray());
    }

    @Operation(
            summary = "Блокирование пользователя",
            description = "Позволяет заблокировать пользователя по его логину с комментарием"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Отправляет на почту сообщение о блокировке"),
        @ApiResponse(responseCode = "403", description = "Пустой логин или комментарий",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorListResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Пользователя с таким логином не существует",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorListResponse.class))})
    }
    )
    @PostMapping("/locked")
    public ResponseEntity<?> lockedUser(@RequestBody @Valid final LockedUserDto lockedUserDto,
                                        final BindingResult bindingResult) {
        final ErrorListResponse response = new ErrorListResponse();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(f -> response.getErrors()
                    .add(Objects.requireNonNull(f.getDefaultMessage())));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (adminService.lockedUser(lockedUserDto)) {
            return ResponseEntity.ok().build();
        } else {
            response.getErrors().add("login: Некорректный логин пользователя");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Разблокировка пользователя",
            description = "Позволяет разблокировать пользователя по его логину"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Возвращает на стартовую страницу"),
        @ApiResponse(responseCode = "403", description = "Пользователя с таким логином не существует",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = ErrorListResponse.class))})
    })
    @PostMapping("/nonLocked")
    public ResponseEntity<?> nonLockedUser(@RequestParam final String login) {
        if (adminService.nonLockedUser(login)) {
            return ResponseEntity.ok().build();
        } else {
            final ErrorListResponse response = new ErrorListResponse();
            response.getErrors().add("login: Некорректный логин пользователя");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
