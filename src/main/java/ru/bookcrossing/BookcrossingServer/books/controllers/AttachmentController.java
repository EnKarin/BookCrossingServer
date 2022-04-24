package ru.bookcrossing.BookcrossingServer.books.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bookcrossing.BookcrossingServer.books.dto.AttachmentDto;
import ru.bookcrossing.BookcrossingServer.books.service.AttachmentService;
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/myBook/save/attachment")
    public ResponseEntity<?> saveAttachment(@ModelAttribute AttachmentDto attachmentDto,
                                            Principal principal) throws IOException {
        ErrorListResponse response = attachmentService.saveAttachment(attachmentDto, principal.getName());
        if(response.getErrors().isEmpty())
            return new ResponseEntity<>(HttpStatus.OK);
        else return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
