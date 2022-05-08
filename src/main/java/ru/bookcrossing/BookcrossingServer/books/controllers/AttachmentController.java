package ru.bookcrossing.BookcrossingServer.books.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bookcrossing.BookcrossingServer.books.dto.AttachmentDto;
import ru.bookcrossing.BookcrossingServer.books.service.AttachmentService;
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;

import java.io.IOException;
import java.security.Principal;

@Tag(
        name = "Вложения к книгам",
        description = "Позволяет добавить или удалить фотографию книги"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/myBook")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Operation(
            summary = "Отправка вложения",
            description = "Позволяет сохранить фотографию книги"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Вложение сохранено")
    })
    @PostMapping("/save/attachment")
    public ResponseEntity<?> saveAttachment(@ModelAttribute AttachmentDto attachmentDto,
                                            Principal principal) throws IOException {
        ErrorListResponse response = attachmentService.saveAttachment(attachmentDto, principal.getName());
        if(response.getErrors().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Operation(
            summary = "Удаление вложения",
            description = "Позволяет удалить фотографию книги"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Вложение удалено")
    })
    @DeleteMapping("/delete/attachment")
    public ResponseEntity<?> deleteAttachment(@RequestParam int bookId,
                                            Principal principal){
        ErrorListResponse response = attachmentService.deleteAttachment(bookId, principal.getName());
        if(response.getErrors().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
