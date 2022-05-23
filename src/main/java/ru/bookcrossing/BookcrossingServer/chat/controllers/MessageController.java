package ru.bookcrossing.BookcrossingServer.chat.controllers;

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
import ru.bookcrossing.BookcrossingServer.chat.dto.MessagePutRequest;
import ru.bookcrossing.BookcrossingServer.chat.dto.MessageRequest;
import ru.bookcrossing.BookcrossingServer.chat.model.Message;
import ru.bookcrossing.BookcrossingServer.chat.service.MessageService;
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;
import ru.bookcrossing.BookcrossingServer.exception.MessageNotFountException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

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
            @ApiResponse(responseCode = "406", description = "Нет доступа к чату",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Сообщение должно содержать хотя бы 1 видимый символ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Сообщение отправлено",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Message.class))})
    })
    @PostMapping
    public ResponseEntity<?> sendMessage(@Valid @RequestBody MessageRequest messageRequest,
                                                  BindingResult bindingResult,
                                                  Principal principal){
        ErrorListResponse response = new ErrorListResponse();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(f -> response.getErrors()
                    .add(Objects.requireNonNull(f.getDefaultMessage())));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Optional<Message> message = messageService.sendMessage(messageRequest, principal.getName());
        if(message.isPresent()){
            return new ResponseEntity<>(message.get(), HttpStatus.OK);
        }
        else {
            response.getErrors().add("correspondence: Нет доступа к чату");
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Operation(
            summary = "Редактирование сообщения",
            description = "Позволяет изменить сообщение в чате"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "406", description = "Нет доступа к чату",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Сообщение не найдено",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Сообщение должно содержать хотя бы 1 видимый символ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Сообщение изменено",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Message.class))})
    })
    @PutMapping
    public ResponseEntity<?> putMessage(@Valid @RequestBody MessagePutRequest messageRequest,
                                        BindingResult bindingResult,
                                        Principal principal){
        ErrorListResponse response = new ErrorListResponse();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(f -> response.getErrors()
                    .add(Objects.requireNonNull(f.getDefaultMessage())));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<Message> message = messageService.putMessage(messageRequest, principal.getName());
            if(message.isPresent()){
                return new ResponseEntity<>(message.get(), HttpStatus.OK);
            }
            else {
                response.getErrors().add("correspondence: Нет доступа к чату");
                return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
            }
        }
        catch (MessageNotFountException e){
            response.getErrors().add("message: Сообщение не найдено");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Удаление сообщения у всех",
            description = "Позволяет удалить сообщение из чата"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Нет доступа",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Сообщение удалено",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Message.class))})
    })
    @DeleteMapping
    public ResponseEntity<?> deleteForEveryoneMessage(@RequestParam long messageId,
                                           Principal principal){
        ErrorListResponse response = messageService.deleteForEveryoneMessage(messageId, principal.getName());
        if(response.getErrors().isEmpty()){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Удаление сообщения у себя",
            description = "Позволяет удалить сообщение из чата"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Нет доступа",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Сообщение удалено",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Message.class))})
    })
    @PostMapping("/deleteForMe")
    public ResponseEntity<?> deleteForMeMessage(@RequestParam long messageId,
                                           Principal principal){
        ErrorListResponse response = messageService.deleteForMeMessage(messageId, principal.getName());
        if(response.getErrors().isEmpty()){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
