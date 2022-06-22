package io.github.enkarin.bookcrossing.mail.service;

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

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;
    private final ActionMailUserRepository confirmationMailUserRepository;
    private final UserRepository userRepository;

    @Value("${server.port}")
    private String port;

    public void sendApproveMail(final User user){
        final String token = UUID.randomUUID().toString();
        final ActionMailUser confirmationMailUser = new ActionMailUser();
        confirmationMailUser.setUser(user);
        confirmationMailUser.setConfirmationMail(token);
        confirmationMailUser.setType(ApproveType.MAIL);
        confirmationMailUserRepository.save(confirmationMailUser);

        final SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Подтверждение регистрации BookCrossing");
        message.setText("Перейдите по ссылке, чтобы подтвердить создание аккаунта: " +
                String.format("https://localhost:%s/registration/confirmation?token=%s", port, token));

        emailSender.send(message);
    }

    public boolean sendResetPassword(final String email){
        final Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            final String token = UUID.randomUUID().toString();
            final ActionMailUser confirmationMailUser = new ActionMailUser();
            confirmationMailUser.setUser(user.get());
            confirmationMailUser.setConfirmationMail(token);
            confirmationMailUser.setType(ApproveType.RESET);
            confirmationMailUserRepository.save(confirmationMailUser);

            final SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Сброс пароля BookCrossing");
            message.setText("Перейдите по ссылке, чтобы сменить пароль: " +
                    String.format("https://localhost:%s/reset/update?token=%s", port, token));

            emailSender.send(message);
            return true;
        } else {
            return false;
        }
    }

    public void sendBlockingMessage(final User user, final String comment){
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Ваш аккаунт заблокирован");
        message.setText("Аккаунт на сервисе BookCrossing был заблокирован администратором. Причина:" + comment);
        emailSender.send(message);
    }

    public void sendUnlockMessage(final User user){
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Ваш аккаунт разблокирован");
        message.setText("Аккаунт на сервисе BookCrossing был разблокирован.");
        emailSender.send(message);
    }

    public void sendAlertsMessage(final User user, final int count){
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Непрочитанные сообщения");
        message.setText(String.format("В вашем аккаунте есть непрочитанные сообщения: %s", count));
        emailSender.send(message);
    }
}
