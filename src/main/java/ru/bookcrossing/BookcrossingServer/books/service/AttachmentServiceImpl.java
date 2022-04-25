package ru.bookcrossing.BookcrossingServer.books.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.books.dto.AttachmentDto;
import ru.bookcrossing.BookcrossingServer.books.model.Attachment;
import ru.bookcrossing.BookcrossingServer.books.model.Book;
import ru.bookcrossing.BookcrossingServer.books.repository.AttachmentRepository;
import ru.bookcrossing.BookcrossingServer.books.repository.BookRepository;
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;
import ru.bookcrossing.BookcrossingServer.user.repository.UserRepository;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AttachmentServiceImpl implements AttachmentService{

    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;
    private final BookRepository bookRepository;

    @Override
    public ErrorListResponse saveAttachment(AttachmentDto attachmentDto, String login) throws IOException {
        ErrorListResponse response = new ErrorListResponse();
        Optional<Book> book = userRepository.findByLogin(login).get().getBooks().stream()
                .filter(b -> b.getId() ==attachmentDto.getBookId()).findFirst();
        if(book.isPresent()){
            Attachment attachment = new Attachment();
            attachment.setData(attachmentDto.getFile().getBytes());
            String fileName = attachmentDto.getFile().getOriginalFilename();
            if(fileName != null) {
                String expansion = fileName.substring(fileName.indexOf('.')).toLowerCase(Locale.ROOT);
                if(expansion.contains("jpeg") || expansion.contains("jpg")
                        || expansion.contains("png") || expansion.contains("bmp")) {
                    attachment.setExpansion(expansion);
                    attachment = attachmentRepository.save(attachment);
                    book.get().setAttachment(attachment);
                    bookRepository.save(book.get());
                }
                else response.getErrors().add("attachment: Недопустимый формат файла");
            }
            else response.getErrors().add("attachment: Имя не должно быть пустым");
        } else{
            response.getErrors().add("attachment: Нет доступа к данной книге");
        }
        return response;
    }
}
