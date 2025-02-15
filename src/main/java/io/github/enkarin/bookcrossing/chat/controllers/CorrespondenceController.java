package io.github.enkarin.bookcrossing.chat.controllers;

import io.github.enkarin.bookcrossing.chat.dto.ChatInfo;
import io.github.enkarin.bookcrossing.chat.dto.MessageDto;
import io.github.enkarin.bookcrossing.chat.dto.UsersCorrKeyDto;
import io.github.enkarin.bookcrossing.chat.service.CorrespondenceService;
import io.github.enkarin.bookcrossing.chat.service.FindChatsService;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.exception.CannotBeCreatedCorrespondenceException;
import io.github.enkarin.bookcrossing.exception.ChatAlreadyCreatedException;
import io.github.enkarin.bookcrossing.exception.ChatNotFoundException;
import io.github.enkarin.bookcrossing.exception.NoAccessToChatException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.security.Principal;
import java.util.Map;

@Tag(
    name = "Чаты",
    description = "Редактирование и создание чатов с пользователями"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/correspondence")
@Validated
public class CorrespondenceController {
    private final FindChatsService findAllChats;
    private final CorrespondenceService correspondenceService;

    private static final String CORRESPONDENCE = "correspondence";
    private static final String USER_ID = "userId";
    private static final String FIRST_USER_ID = "firstUserId";
    private static final String SECOND_USER_ID = "secondUserId";

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
                schema = @Schema(implementation = UsersCorrKeyDto.class))})
    })
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = USER_ID, description = "Идентификатор пользователя")
    })
    @PostMapping
    public ResponseEntity<UsersCorrKeyDto> createCorrespondence(@RequestHeader(USER_ID) @NotBlank(message = "не должно быть пустым") final String userId,
                                                                final Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(correspondenceService.createChat(Integer.parseInt(userId), principal.getName()));
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
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = USER_ID, description = "Идентификатор пользователя")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteCorrespondence(@RequestHeader(USER_ID) @NotBlank(message = "не должно быть пустым") final String userId, final Principal principal) {
        correspondenceService.deleteChat(Integer.parseInt(userId), principal.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Получение чата",
        description = "Позволяет получить чат с выбранным пользователем"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Чата не существует",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "403", description = "Нет доступа к чату",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Возвращает список сообщений",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, array = @ArraySchema(schema = @Schema(implementation = MessageDto.class)))})
    }
    )
    @GetMapping
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = FIRST_USER_ID, description = "Идентификатор первого пользователя"),
        @Parameter(in = ParameterIn.HEADER, name = SECOND_USER_ID, description = "Идентификатор второго пользователя")
    })
    public ResponseEntity<Object[]> getCorrespondence(@RequestHeader(FIRST_USER_ID) @NotBlank(message = "не должно быть пустым") final String firstUserId,
                                                      @RequestHeader(SECOND_USER_ID) @NotBlank(message = "не должно быть пустым") final String secondUserId,
                                                      @RequestParam final int zone,
                                                      final Principal principal) {
        return ResponseEntity.ok(correspondenceService.getChat(Integer.parseInt(firstUserId), Integer.parseInt(secondUserId), zone, principal.getName()).toArray());
    }

    @Operation(summary = "Поиск всех чатов пользователя", description = "Позволяет получить краткую информацию о всех чатах пользователя")
    @ApiResponse(responseCode = "200", description = "Возвращает список чатов",
        content = {@Content(mediaType = Constant.MEDIA_TYPE, array = @ArraySchema(schema = @Schema(implementation = ChatInfo.class)))})
    @GetMapping("/all")
    public ChatInfo[] findAllChats(@RequestParam final int pageNumber, @RequestParam final int pageSize, final Principal principal) {
        return findAllChats.findAllChats(pageNumber, pageSize, principal.getName());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ChatNotFoundException.class)
    public Map<String, String> chatNotFound(final ChatNotFoundException exc) {
        return Map.of(CORRESPONDENCE, exc.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ChatAlreadyCreatedException.class)
    public Map<String, String> chatAlreadyCreated(final ChatAlreadyCreatedException exc) {
        return Map.of(CORRESPONDENCE, exc.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(CannotBeCreatedCorrespondenceException.class)
    public Map<String, String> userIsLocked(final CannotBeCreatedCorrespondenceException exc) {
        return Map.of("user", exc.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NoAccessToChatException.class)
    public Map<String, String> chatNoAccess(final NoAccessToChatException exc) {
        return Map.of(CORRESPONDENCE, exc.getMessage());
    }
}
