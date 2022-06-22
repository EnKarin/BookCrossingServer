package io.github.enkarin.bookcrossing.chat.controllers;

import io.github.enkarin.bookcrossing.chat.dto.MessageResponse;
import io.github.enkarin.bookcrossing.chat.dto.UsersCorrKeyDto;
import io.github.enkarin.bookcrossing.chat.dto.ZonedUserCorrKeyDto;
import io.github.enkarin.bookcrossing.chat.model.Correspondence;
import io.github.enkarin.bookcrossing.chat.service.CorrespondenceService;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.errors.ErrorListResponse;
import io.github.enkarin.bookcrossing.exception.ChatAlreadyCreatedException;
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
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "406", description = "Нельзя создать чат с данным пользователем",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Чат создан",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = Correspondence.class))})
    })
    @PostMapping
    public ResponseEntity<?> createCorrespondence(@RequestParam final int userId,
                                                  final Principal principal){
        final ErrorListResponse response = new ErrorListResponse();
        try {
            final Optional<UsersCorrKeyDto> usersCorrKeyDto = correspondenceService.createChat(userId, principal.getName());
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
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Чат удален")
    }
    )
    @DeleteMapping
    public ResponseEntity<?> deleteCorrespondence(@RequestParam @Parameter(description = "Идентификатор пользователя")
                                                      final int userId,
                                                  final Principal principal){
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
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Возвращает список сообщений",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = MessageResponse.class))})
    }
    )
    @GetMapping
    public ResponseEntity<?> getCorrespondence(@RequestBody final ZonedUserCorrKeyDto dto,
                                               final Principal principal){
        final Optional<List<MessageResponse>> messageResponse = correspondenceService.getChat(dto, principal.getName());
        if(messageResponse.isPresent()){
            return ResponseEntity.ok(messageResponse.get());
        }
        else {
            final ErrorListResponse response = new ErrorListResponse();
            response.getErrors().add("correspondence: Чата не существует");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
