package io.github.enkarin.bookcrossing.admin.controllers;

import io.github.enkarin.bookcrossing.admin.dto.InfoUsersDto;
import io.github.enkarin.bookcrossing.admin.dto.LockedUserDto;
import io.github.enkarin.bookcrossing.admin.service.AdminService;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.exception.BindingErrorsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

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
    @GetMapping
    public ResponseEntity<List<InfoUsersDto>> userList(@RequestParam @Parameter(description = "Часовой пояс") final int zone) {
        return ResponseEntity.ok(adminService.findAllUsers(zone));
    }

    @Operation(
            summary = "Блокирование пользователя",
            description = "Позволяет заблокировать пользователя по его логину с комментарием"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Отправляет на почту сообщение о блокировке"),
        @ApiResponse(responseCode = "406", description = "Пустой логин или комментарий",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "404", description = "Пользователя с таким логином не существует",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(ref = "#/components/schemas/NewErrorBody"))})
    }
    )
    @PostMapping("/locked")
    public ResponseEntity<Void> lockedUser(@RequestBody @Valid final LockedUserDto lockedUserDto,
                                        final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            final List<String> response = new LinkedList<>();
            bindingResult.getAllErrors().forEach(f -> response.add(f.getDefaultMessage()));
            throw new BindingErrorsException(response);
        }
        adminService.lockedUser(lockedUserDto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Разблокировка пользователя",
            description = "Позволяет разблокировать пользователя по его логину"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", description = "Пользователя с таким логином не существует",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(ref = "#/components/schemas/NewErrorBody"))})
    })
    @PostMapping("/nonLocked")
    public ResponseEntity<Void> nonLockedUser(@RequestParam final String login) {
        adminService.nonLockedUser(login);
        return ResponseEntity.ok().build();
    }
}
