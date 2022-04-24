package ru.bookcrossing.BookcrossingServer.books.service;

import ru.bookcrossing.BookcrossingServer.books.dto.AttachmentDto;
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;

import java.io.IOException;

public interface AttachmentService {

    ErrorListResponse saveAttachment(AttachmentDto attachmentDto, String login) throws IOException;
}
