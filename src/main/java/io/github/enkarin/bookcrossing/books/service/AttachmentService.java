package io.github.enkarin.bookcrossing.books.service;

import io.github.enkarin.bookcrossing.books.dto.AttachmentDto;
import io.github.enkarin.bookcrossing.books.dto.AttachmentMultipartDto;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.books.enums.FormatType;
import io.github.enkarin.bookcrossing.books.exceptions.NoAccessToAttachmentException;
import io.github.enkarin.bookcrossing.books.model.Attachment;
import io.github.enkarin.bookcrossing.books.model.Book;
import io.github.enkarin.bookcrossing.books.repository.AttachmentRepository;
import io.github.enkarin.bookcrossing.books.repository.BookRepository;
import io.github.enkarin.bookcrossing.constant.ErrorMessage;
import io.github.enkarin.bookcrossing.exception.AttachmentNotFoundException;
import io.github.enkarin.bookcrossing.exception.BookNotFoundException;
import io.github.enkarin.bookcrossing.exception.UnsupportedImageTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;

import static io.github.enkarin.bookcrossing.utils.ImageCompressor.compressImage;

@RequiredArgsConstructor
@Service
@Transactional
public class AttachmentService {
    private final AttachmentRepository attachRepository;
    private final BookRepository bookRepository;

    public AttachmentDto findAttachmentData(final int id, final FormatType imageFormat) {
        return AttachmentDto.fromAttachment(attachRepository.findById(id).orElseThrow(AttachmentNotFoundException::new), imageFormat);
    }

    public BookModelDto saveTitleAttachment(final AttachmentMultipartDto attachmentMultipartDto, final String login) {
        final Book book = bookRepository.findBooksByOwnerLoginAndBookId(login, attachmentMultipartDto.getBookId()).orElseThrow(BookNotFoundException::new);
        book.setTitleAttachment(saveAttachment(book, attachmentMultipartDto));
        return BookModelDto.fromBook(book);
    }

    public BookModelDto saveAdditionalAttachment(final AttachmentMultipartDto attachmentMultipartDto, final String login) {
        final Book book = bookRepository.findBooksByOwnerLoginAndBookId(login, attachmentMultipartDto.getBookId()).orElseThrow(BookNotFoundException::new);
        saveAttachment(book, attachmentMultipartDto);
        return BookModelDto.fromBook(book);
    }

    private Attachment saveAttachment(final Book book, final AttachmentMultipartDto attachmentMultipartDto) {
        final String fileName = attachmentMultipartDto.getFile().getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new UnsupportedImageTypeException(ErrorMessage.ERROR_3001.getCode());
        } else {
            final String expansion = fileName.substring(fileName.indexOf('.') + 1).toLowerCase(Locale.ROOT);
            if ("jpeg".equals(expansion) || "jpg".equals(expansion) || "png".equals(expansion) || "bmp".equals(expansion)) {
                return createOrUpdateAttachment(book, attachmentMultipartDto.getFile(), expansion);
            } else {
                throw new UnsupportedImageTypeException(ErrorMessage.ERROR_3002.getCode());
            }
        }
    }

    private Attachment createOrUpdateAttachment(final Book book, final MultipartFile multipartFile, final String expansion) {
        try {
            final Attachment attachment = new Attachment();
            attachment.setBook(book);
            attachment.setOriginalImageExpansion(expansion);
            final BufferedImage image = ImageIO.read(multipartFile.getInputStream());
            attachment.setOriginalImage(multipartFile.getBytes());
            attachment.setListImage(compressImage(image, 200, 300));
            attachment.setThumbImage(compressImage(image, 70, 70));
            return attachRepository.save(attachment);
        } catch (IOException e) {
            throw new UnsupportedImageTypeException(ErrorMessage.ERROR_2008.getCode(), e);
        }
    }

    public void deleteAttachment(final int id, final String login) {
        final Attachment attachment = attachRepository.findById(id).orElseThrow(AttachmentNotFoundException::new);
        if (attachment.getBook().getOwner().getLogin().equals(login)) {
            attachRepository.delete(attachment);
        } else {
            throw new NoAccessToAttachmentException();
        }
    }
}
