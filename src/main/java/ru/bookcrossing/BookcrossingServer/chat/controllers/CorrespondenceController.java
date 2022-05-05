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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bookcrossing.BookcrossingServer.chat.service.CorrespondenceService;
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;
import ru.bookcrossing.BookcrossingServer.exception.ChatAlreadyCreatedException;

import java.security.Principal;
import java.util.Optional;

@Tag(
        name = "Чаты",
        description = "Позволяет создавать чаты с пользователями"
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
            @ApiResponse(responseCode = "200", description = "Чат создан")
    }
    )
    @PostMapping("/create")
    public ResponseEntity<?> createCorrespondence(@RequestParam int userId,
                                                  Principal principal){
        try {
            Optional<ErrorListResponse> response = correspondenceService.createChat(userId, principal.getName());
            if(response.isEmpty()){
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
            }
        }
        catch (ChatAlreadyCreatedException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
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
    @PostMapping("/delete")
    public ResponseEntity<?> deleteCorrespondence(@RequestParam int userId,
                                                  Principal principal){
        if(correspondenceService.deleteChat(userId, principal.getName())){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
