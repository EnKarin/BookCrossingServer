package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.AttachmentDto;
import io.github.enkarin.bookcrossing.books.model.Attachment;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.books.repository.AttachmentRepository;
import io.github.enkarin.bookcrossing.books.repository.BookRepository;
import io.github.enkarin.bookcrossing.errors.ErrorListResponse;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AttachmentService {

    private final UserRepository userRepository;
    private final AttachmentRepository attachRepository;
    private final BookRepository bookRepository;

    public ErrorListResponse saveAttachment(final AttachmentDto attachmentDto, final String login) throws IOException {
        final ErrorListResponse response = new ErrorListResponse();
        final Optional<Book> book = userRepository.findByLogin(login).orElseThrow().getBooks().stream()
                .filter(b -> b.getBookId() == attachmentDto.getBookId())
                .findFirst();
        if (book.isPresent()){
            Attachment attachment = new Attachment();
            attachment.setData(attachmentDto.getFile().getBytes());
            final String fileName = attachmentDto.getFile().getOriginalFilename();
            if (fileName == null) {
                response.getErrors().add("attachment: Имя не должно быть пустым");
            } else {
                final String expansion = fileName.substring(fileName.indexOf('.')).toLowerCase(Locale.ROOT);
                if (expansion.contains("jpeg") || expansion.contains("jpg") ||
                        expansion.contains("png") || expansion.contains("bmp")) {
                    attachment.setExpansion(expansion);
                    attachment = attachRepository.save(attachment);
                    book.get().setAttachment(attachment);
                    bookRepository.save(book.get());
                } else {
                    response.getErrors().add("attachment: Недопустимый формат файла");
                }
            }
        } else {
            response.getErrors().add("attachment: Нет доступа к данной книге");
        }
        return response;
    }

    public ErrorListResponse deleteAttachment(final int bookId, final String login){
        final ErrorListResponse response = new ErrorListResponse();
        final Optional<Book> book = userRepository.findByLogin(login).orElseThrow().getBooks().stream()
                .filter(b -> b.getBookId() == bookId).findFirst();
        if (book.isPresent()){
            final Optional<Attachment> attachment = Optional.ofNullable(book.get().getAttachment());
            if (attachment.isPresent()) {
                attachRepository.delete(book.get().getAttachment());
            }
        } else {
            response.getErrors().add("attachment: Нет доступа к данной книге");
        }
        return response;
    }
}
