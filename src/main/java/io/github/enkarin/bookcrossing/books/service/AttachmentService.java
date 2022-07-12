package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.AttachmentDto;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.books.model.Attachment;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.books.repository.AttachmentRepository;
import io.github.enkarin.bookcrossing.books.repository.BookRepository;
import io.github.enkarin.bookcrossing.exception.AttachmentNotFoundException;
import io.github.enkarin.bookcrossing.exception.BadRequestException;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class AttachmentService {

    private final UserRepository userRepository;
    private final AttachmentRepository attachRepository;
    private final BookRepository bookRepository;

    public BookModelDto saveAttachment(final AttachmentDto attachmentDto, final String login) throws IOException {
        final Book book = userRepository.findByLogin(login).orElseThrow().getBooks().stream()
                .filter(b -> b.getBookId() == attachmentDto.getBookId())
                .findFirst()
                .orElseThrow(BookNotFoundException::new);
        final String fileName = attachmentDto.getFile().getOriginalFilename();
        if (fileName == null || fileName.equals("")) {
            throw new BadRequestException("Имя не должно быть пустым");
        } else {
            final String expansion = fileName.substring(fileName.indexOf('.')).toLowerCase(Locale.ROOT);
            if (expansion.contains("jpeg") || expansion.contains("jpg") ||
                    expansion.contains("png") || expansion.contains("bmp")) {
                final Attachment attachment = new Attachment();
                attachment.setData(attachmentDto.getFile().getBytes());
                attachment.setBook(book);
                attachment.setExpansion(expansion);
                book.setAttachment(attachment);
                attachRepository.save(attachment);
                return BookModelDto.fromBook(bookRepository.getById(book.getBookId()));
            } else {
                throw new BadRequestException("Недопустимый формат файла");
            }
        }
    }

    public void deleteAttachment(final int bookId, final String login) {
        final Book book = userRepository.findByLogin(login).orElseThrow().getBooks().stream()
                .filter(b -> b.getBookId() == bookId)
                .findFirst()
                .orElseThrow(BookNotFoundException::new);
        Optional.ofNullable(book.getAttachment())
                .orElseThrow(AttachmentNotFoundException::new);
        attachRepository.deleteById(book.getAttachment().getAttachId());
    }
}
