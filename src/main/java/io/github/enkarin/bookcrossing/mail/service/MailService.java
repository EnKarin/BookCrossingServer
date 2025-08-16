package io.github.enkarin.bookcrossing.mail.service;

import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.mail.enums.ApproveType;
import io.github.enkarin.bookcrossing.mail.model.ActionMailUser;
import io.github.enkarin.bookcrossing.mail.repository.ActionMailUserRepository;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Nonnull;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MailService {

    private final JavaMailSender emailSender;
    private final ActionMailUserRepository confirmationMailUserRepository;
    private final UserRepository userRepository;

    @Value("${server.port}")
    private String port;
    @Value("${spring.mail.from}")
    private String fromEmail;

    @Transactional
    public void sendApproveMail(final User user) {
        final String token = UUID.randomUUID().toString();
        final ActionMailUser confirmationMailUser = new ActionMailUser();
        confirmationMailUser.setUser(user);
        confirmationMailUser.setConfirmationMail(token);
        confirmationMailUser.setType(ApproveType.MAIL);
        confirmationMailUserRepository.save(confirmationMailUser);

        final var message = prepareMailMessage(user.getEmail());
        message.setSubject("Подтверждение регистрации BookCrossing");
        message.setText("Перейдите по ссылке, чтобы подтвердить создание аккаунта: " +
                "https://localhost:%s/registration/confirmation?token=%s".formatted(port, token));
        emailSender.send(message);
    }

    @Transactional
    public void sendResetPassword(final String email) {
        final User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        final String token = UUID.randomUUID().toString();
        final ActionMailUser confirmationMailUser = new ActionMailUser();
        confirmationMailUser.setUser(user);
        confirmationMailUser.setConfirmationMail(token);
        confirmationMailUser.setType(ApproveType.RESET);
        confirmationMailUserRepository.save(confirmationMailUser);

        final var message = prepareMailMessage(email);
        message.setSubject("Сброс пароля BookCrossing");
        message.setText("Перейдите по ссылке, чтобы сменить пароль: " +
                "https://localhost:%s/reset/update?token=%s".formatted(port, token));
        emailSender.send(message);
    }

    public void sendBlockingMessage(final User user, final String comment) {
        final var message = prepareMailMessage(user.getEmail());
        message.setSubject("Ваш аккаунт заблокирован");
        message.setText("Аккаунт на сервисе BookCrossing был заблокирован администратором. Причина:" + comment);
        emailSender.send(message);
    }

    public void sendUnlockMessage(final User user) {
        final var message = prepareMailMessage(user.getEmail());
        message.setSubject("Ваш аккаунт разблокирован");
        message.setText("Аккаунт на сервисе BookCrossing был разблокирован.");
        emailSender.send(message);
    }

    public void sendAlertsMessage(final User user, final int count) {
        final var message = prepareMailMessage(user.getEmail());
        message.setSubject("Непрочитанные сообщения");
        message.setText("В вашем аккаунте есть непрочитанные сообщения: %s".formatted(count));
        emailSender.send(message);
    }

    @Nonnull
    private SimpleMailMessage prepareMailMessage(@Nonnull final String toEmail) {
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom(fromEmail);
        return message;
    }
}
