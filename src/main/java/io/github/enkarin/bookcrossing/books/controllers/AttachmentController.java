package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.AttachmentDto;
import io.github.enkarin.bookcrossing.books.service.AttachmentService;
import io.github.enkarin.bookcrossing.constant.Constant;
import io.github.enkarin.bookcrossing.exception.AttachmentNotFoundException;
import io.github.enkarin.bookcrossing.exception.BadRequestException;
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
import java.util.Map;

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
        @ApiResponse(responseCode = "415", description = "Некорректное вложение",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "404", description = "Книги не существует",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "201", description = "Вложение сохранено")
    })
    @PostMapping("/attachment")
    public ResponseEntity<?> saveAttachment(@ModelAttribute final AttachmentDto attachmentDto,
                                            final Principal principal) throws IOException {
        attachmentService.saveAttachment(attachmentDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Удаление вложения",
            description = "Позволяет удалить фотографию книги"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Вложения или книги не существует",
            content = {@Content(mediaType = Constant.MEDIA_TYPE,
                    schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Вложение удалено")
    })
    @DeleteMapping("/attachment")
    public ResponseEntity<?> deleteAttachment(@RequestParam final int bookId,
                                            final Principal principal) {
        attachmentService.deleteAttachment(bookId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(BadRequestException.class)
    public Map<String, String> badRequest(final BadRequestException exc) {
        return Map.of("attachment", exc.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AttachmentNotFoundException.class)
    public Map<String, String> attachNotFound(final AttachmentNotFoundException exc) {
        return Map.of("attachment", exc.getMessage());
    }
}
