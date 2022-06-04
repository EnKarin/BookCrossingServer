package ru.bookcrossing.BookcrossingServer.chat.controllers;

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
import ru.bookcrossing.BookcrossingServer.chat.dto.MessageResponse;
import ru.bookcrossing.BookcrossingServer.chat.dto.UsersCorrKeyDto;
import ru.bookcrossing.BookcrossingServer.chat.dto.ZonedUserCorrKeyDto;
import ru.bookcrossing.BookcrossingServer.chat.model.Correspondence;
import ru.bookcrossing.BookcrossingServer.chat.service.CorrespondenceService;
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;
import ru.bookcrossing.BookcrossingServer.exception.ChatAlreadyCreatedException;
import ru.bookcrossing.BookcrossingServer.exception.UserNotFoundException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "406", description = "Нельзя создать чат с данным пользователем",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Чат создан",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Correspondence.class))})
    })
    @PostMapping
    public ResponseEntity<?> createCorrespondence(@RequestParam int userId,
                                                  Principal principal){
        ErrorListResponse response = new ErrorListResponse();
        try {
            Optional<UsersCorrKeyDto> usersCorrKeyDto = correspondenceService.createChat(userId, principal.getName());
            if(usersCorrKeyDto.isPresent()){
                return new ResponseEntity<>(usersCorrKeyDto.get(), HttpStatus.OK);
            }
            else {
                response.getErrors().add("user: Пользователь заблокирован");
                return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
            }
        }
        catch (ChatAlreadyCreatedException e){
            response.getErrors().add("correspondence: Чат уже существует");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        catch (UserNotFoundException e){
            response.getErrors().add("user: Пользователь не найден");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Удаление чата",
            description = "Позволяет удалить чат с выбранным пользователем"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Чата не существует",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Чат удален")
    }
    )
    @DeleteMapping
    public ResponseEntity<?> deleteCorrespondence(@RequestParam @Parameter(description = "Идентификатор пользователя")
                                                              int userId,
                                                  Principal principal){
        if(correspondenceService.deleteChat(userId, principal.getName())){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Получение чата",
            description = "Позволяет получить чат с выбранным пользователем"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Чата не существует",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Возвращает список сообщений",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))})
    }
    )
    @GetMapping
    public ResponseEntity<?> getCorrespondence(@RequestBody ZonedUserCorrKeyDto dto,
                                               Principal principal){
        Optional<List<MessageResponse>> messageResponse = correspondenceService.getChat(dto, principal.getName());
        if(messageResponse.isPresent()){
            return ResponseEntity.ok(messageResponse.get());
        }
        else {
            ErrorListResponse response = new ErrorListResponse();
            response.getErrors().add("correspondence: Чата не существует");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
