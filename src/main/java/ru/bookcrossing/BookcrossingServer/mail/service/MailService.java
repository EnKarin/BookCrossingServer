package ru.bookcrossing.BookcrossingServer.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.mail.enums.ApproveType;
import ru.bookcrossing.BookcrossingServer.mail.model.ActionMailUser;
import ru.bookcrossing.BookcrossingServer.mail.repository.ActionMailUserRepository;
import ru.bookcrossing.BookcrossingServer.user.model.User;
import ru.bookcrossing.BookcrossingServer.user.repository.UserRepository;

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

    public void sendApproveMail(User user){
        String token = UUID.randomUUID().toString();
        ActionMailUser confirmationMailUser = new ActionMailUser();
        confirmationMailUser.setUser(user);
        confirmationMailUser.setConfirmationMail(token);
        confirmationMailUser.setType(ApproveType.MAIL);
        confirmationMailUserRepository.save(confirmationMailUser);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Подтверждение регистрации BookCrossing");
        message.setText("Перейдите по ссылке, чтобы подтвердить создание аккаунта: "
                + String.format("https://localhost:%s/registration/confirmation?token=%s", port, token));

        emailSender.send(message);
    }

    public boolean sendResetPassword(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()) {
            String token = UUID.randomUUID().toString();
            ActionMailUser confirmationMailUser = new ActionMailUser();
            confirmationMailUser.setUser(user.get());
            confirmationMailUser.setConfirmationMail(token);
            confirmationMailUser.setType(ApproveType.RESET);
            confirmationMailUserRepository.save(confirmationMailUser);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Сброс пароля BookCrossing");
            message.setText("Перейдите по ссылке, чтобы сменить пароль: "
                    + String.format("https://localhost:%s/reset/update?token=%s", port, token));

            emailSender.send(message);
            return true;
        }
        else return false;
    }

    public void sendBlockingMessage(User user, String comment){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Ваш аккаунт заблокирован");
        message.setText("Аккаунт на сервисе BookCrossing был заблокирован администратором. Причина:"
                + comment);
        emailSender.send(message);
    }

    public void sendUnlockMessage(User user){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Ваш аккаунт разблокирован");
        message.setText("Аккаунт на сервисе BookCrossing был разблокирован.");
        emailSender.send(message);
    }
}
