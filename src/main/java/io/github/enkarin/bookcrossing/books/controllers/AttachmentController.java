package io.github.enkarin.bookcrossing.books.controllers;

import io.github.enkarin.bookcrossing.books.dto.AttachmentMultipartDto;
import io.github.enkarin.bookcrossing.books.exceptions.NoAccessToAttachmentException;
import io.github.enkarin.bookcrossing.books.exceptions.UnsupportedFormatException;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Tag(
    name = "Вложения к книгам",
    description = "Позволяет добавить или удалить фотографию книги"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/myBook/attachment")
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
    @PostMapping
    public ResponseEntity<Void> saveAttachment(@ModelAttribute final AttachmentMultipartDto attachmentMultipartDto, final Principal principal) throws IOException {
        attachmentService.saveAttachment(attachmentMultipartDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Получение вложения", description = "Позволяет получить вложение по идентификатору и требуемому формату")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Запрошен некорректный формат изображения",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "404", description = "Вложения не существует",
            content = {@Content(mediaType = Constant.MEDIA_TYPE, schema = @Schema(ref = "#/components/schemas/NewErrorBody"))}),
        @ApiResponse(responseCode = "200", description = "Вложение отправлено", content = @Content(mediaType = Constant.JPG))
    })
    @GetMapping
    public void findAttachment(@RequestParam final int id, @RequestParam final String format, final HttpServletResponse response) throws IOException {
        final byte[] image = attachmentService.findAttachmentData(id, format);
        response.setContentType("image/jpeg");
        response.setHeader("Content-Disposition", "inline; filename=\"" + id + ".jpg\"");
        response.setContentLength(image.length);
        FileCopyUtils.copy(new ByteArrayInputStream(image), response.getOutputStream());
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
    @DeleteMapping
    public ResponseEntity<Void> deleteAttachment(@RequestParam final int id, final Principal principal) {
        attachmentService.deleteAttachment(id, principal.getName());
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnsupportedFormatException.class)
    public Map<String, String> formatNotFound(final UnsupportedFormatException exception) {
        return Map.of("format", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NoAccessToAttachmentException.class)
    public Map<String, String> attachAccessForbidden(final NoAccessToAttachmentException exception) {
        return Map.of("id", exception.getMessage());
    }
}
