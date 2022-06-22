package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.AttachmentDto;
import io.github.enkarin.bookcrossing.books.service.AttachmentService;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.errors.ErrorListResponse;
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
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Вложение сохранено")
    })
    @PostMapping("/attachment")
    public ResponseEntity<?> saveAttachment(@ModelAttribute final AttachmentDto attachmentDto,
                                            final Principal principal) throws IOException {
        final ErrorListResponse response = attachmentService.saveAttachment(attachmentDto, principal.getName());
        if(response.getErrors().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Удаление вложения",
            description = "Позволяет удалить фотографию книги"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                    content = {@Content(mediaType = Constant.MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorListResponse.class))}),
            @ApiResponse(responseCode = "200", description = "Вложение удалено")
    })
    @DeleteMapping("/attachment")
    public ResponseEntity<?> deleteAttachment(@RequestParam final int bookId,
                                            final Principal principal){
        final ErrorListResponse response = attachmentService.deleteAttachment(bookId, principal.getName());
        if(response.getErrors().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
