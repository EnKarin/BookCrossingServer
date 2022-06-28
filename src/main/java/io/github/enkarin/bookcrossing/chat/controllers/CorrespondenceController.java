package io.github.enkarin.bookcrossing.chat.controllers;

import io.github.enkarin.bookcrossing.chat.dto.MessageDto;
import io.github.enkarin.bookcrossing.chat.dto.UsersCorrKeyDto;
import io.github.enkarin.bookcrossing.chat.dto.ZonedUserCorrKeyDto;
import io.github.enkarin.bookcrossing.chat.model.Correspondence;
import io.github.enkarin.bookcrossing.chat.service.CorrespondenceService;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.exception.CannotBeCreatedCorrespondenceException;
import io.github.enkarin.bookcrossing.exception.ChatAlreadyCreatedException;
import io.github.enkarin.bookcrossing.exception.ChatNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
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
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Tag(
        name = "Чаты",
        description = "Редактирование и создание чатов с пользователями"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/correspondence")
public class CorrespondenceController {

    private final CorrespondenceService correspondenceService;

    @Operation(
            summary = "Создание чата",
            description = "Позволяет создать чат с выбранным пользователем"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "409", description = "Чат уже существует",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "406", description = "Нельзя создать чат с данным пользователем",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "201", description = "Чат создан",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = Correspondence.class))})
    })
    @PostMapping
    public ResponseEntity<?> createCorrespondence(@RequestParam final int userId,
                                                  final Principal principal) {
        final UsersCorrKeyDto usersCorrKeyDto = correspondenceService.createChat(userId, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(usersCorrKeyDto);
    }

    @Operation(
            summary = "Удаление чата",
            description = "Позволяет удалить чат с выбранным пользователем"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Чата не существует",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Чат удален")
        }
    )
    @DeleteMapping
    public ResponseEntity<?> deleteCorrespondence(@RequestParam @Parameter(description = "Идентификатор пользователя")
                                                      final int userId,
                                                  final Principal principal) {
        correspondenceService.deleteChat(userId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Получение чата",
            description = "Позволяет получить чат с выбранным пользователем"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Чата не существует",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Возвращает список сообщений",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = MessageDto.class))})
        }
    )
    @GetMapping
    public ResponseEntity<?> getCorrespondence(@RequestBody final ZonedUserCorrKeyDto dto,
                                               final Principal principal) {
        final List<MessageDto> messageResponse = correspondenceService.getChat(dto, principal.getName());
        return ResponseEntity.ok(messageResponse);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public Map<String, String> userNotFound(final UserNotFoundException exc) {
        return Collections.singletonMap("user:", exc.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ChatNotFoundException.class)
    public Map<String, String> chatNotFound(final ChatNotFoundException exc) {
        return Collections.singletonMap("correspondence:", exc.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ChatAlreadyCreatedException.class)
    public Map<String, String> chatAlreadyCreated(final ChatAlreadyCreatedException exc) {
        return Collections.singletonMap("correspondence:", exc.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(CannotBeCreatedCorrespondenceException.class)
    public Map<String, String> userIsLocked(final CannotBeCreatedCorrespondenceException exc) {
        return Collections.singletonMap("user:", exc.getMessage());
    }
}
