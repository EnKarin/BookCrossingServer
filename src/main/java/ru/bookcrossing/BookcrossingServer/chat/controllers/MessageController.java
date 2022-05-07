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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bookcrossing.BookcrossingServer.chat.dto.MessageDto;
import ru.bookcrossing.BookcrossingServer.chat.model.Message;
import ru.bookcrossing.BookcrossingServer.chat.service.MessageService;
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;

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
    }
    )
    @PostMapping("/send")
    public ResponseEntity<?> createCorrespondence(@Valid @RequestBody MessageDto messageDto,
                                                  BindingResult bindingResult,
                                                  Principal principal){
        ErrorListResponse response = new ErrorListResponse();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(f -> response.getErrors()
                    .add(Objects.requireNonNull(f.getDefaultMessage())));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Optional<Message> message = messageService.sendMessage(messageDto, principal.getName());
        if(message.isPresent()){
            return new ResponseEntity<>(message.get(), HttpStatus.OK);
        }
        else {
            response.getErrors().add("correspondence: Нет доступа к чату");
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
