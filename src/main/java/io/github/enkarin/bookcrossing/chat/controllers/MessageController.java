package io.github.enkarin.bookcrossing.chat.controllers;

import io.github.enkarin.bookcrossing.chat.dto.MessageDto;
import io.github.enkarin.bookcrossing.chat.dto.MessagePutRequest;
import io.github.enkarin.bookcrossing.chat.dto.MessageRequest;
import io.github.enkarin.bookcrossing.chat.service.MessageService;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.exception.*;
import io.swagger.v3.oas.annotations.Operation;
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
import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@Tag(
        name = "Сообщения",
        description = "Позволяет отправлять сообщения в чаты"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/correspondence/message")
public class MessageController {

    private final MessageService messageService;

    @Operation(
            summary = "Отправка сообщения",
            description = "Позволяет отправить сообщение в чат"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "406", description = "Сообщение должно содержать хотя бы 1 видимый символ",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "404", description = "Чата не существует",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "403", description = "Нет доступа к чату",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "201", description = "Сообщение отправлено",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = MessageDto.class))})
    })
    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(@Valid @RequestBody final MessageRequest messageRequest,
                                                  final BindingResult bindingResult,
                                                  final Principal principal) {
        if (bindingResult.hasErrors()) {
            throw new MessageContentException();
        }
        return ResponseEntity.ok(messageService.sendMessage(messageRequest, principal.getName()));
    }

    @Operation(
            summary = "Редактирование сообщения",
            description = "Позволяет изменить сообщение в чате"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "406", description = "Сообщение должно содержать хотя бы 1 видимый символ",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "404", description = "Сообщение не найдено",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "403", description = "Нет доступа к чату",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Сообщение изменено",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(implementation = MessageDto.class))})
    })
    @PutMapping
    public ResponseEntity<MessageDto> putMessage(@Valid @RequestBody final MessagePutRequest messageRequest,
                                        final BindingResult bindingResult,
                                        final Principal principal) {
        if (bindingResult.hasErrors()) {
            throw new MessageNotFountException();
        }
        return ResponseEntity.ok(messageService.putMessage(messageRequest, principal.getName()));
    }

    @Operation(
            summary = "Удаление сообщения у всех",
            description = "Позволяет удалить сообщение из чата"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Сообщение не найдено",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "403", description = "Пользователь не является отправителем",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Сообщение удалено")
    })
    @DeleteMapping
    public ResponseEntity<?> deleteForEveryoneMessage(@RequestParam final long messageId,
                                           final Principal principal) {
        messageService.deleteForEveryoneMessage(messageId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Удаление сообщения у себя",
            description = "Позволяет удалить сообщение из чата"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Сообщение не найдено",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "403", description = "Нет доступа к чату",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Сообщение удалено")
    })
    @DeleteMapping("/deleteForMe")
    public ResponseEntity<?> deleteForMeMessage(@RequestParam final long messageId,
                                           final Principal principal) {
        messageService.deleteForMeMessage(messageId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(MessageContentException.class)
    public Map<String, String> content(final MessageContentException exc) {
        return Collections.singletonMap("message", exc.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ChatNotFoundException.class)
    public Map<String, String> chatNotFound(final ChatNotFoundException exc) {
        return Collections.singletonMap("correspondence", exc.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MessageNotFountException.class)
    public Map<String, String> messageNotFound(final MessageNotFountException exc) {
        return Collections.singletonMap("message", exc.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NoAccessToChatException.class)
    public Map<String, String> noAccess(final NoAccessToChatException exc) {
        return Collections.singletonMap("correspondence", exc.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UserIsNotSenderException.class)
    public Map<String, String> noSender(final UserIsNotSenderException exc) {
        return Collections.singletonMap("correspondence", exc.getMessage());
    }
}
