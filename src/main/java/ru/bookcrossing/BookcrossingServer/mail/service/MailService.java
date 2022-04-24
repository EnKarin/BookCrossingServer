package ru.bookcrossing.BookcrossingServer.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.mail.model.ConfirmationMailUser;
import ru.bookcrossing.BookcrossingServer.mail.repository.ConfirmationMailUserRepository;
import ru.bookcrossing.BookcrossingServer.user.model.User;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;
    private final ConfirmationMailUserRepository repository;

    @Value("${server.port}")
    private String port;

    public void sendMail(User user){
        String token = UUID.randomUUID().toString();
        ConfirmationMailUser confirmationMailUser = new ConfirmationMailUser();
        confirmationMailUser.setUser(user);
        confirmationMailUser.setConfirmationMail(token);
        repository.save(confirmationMailUser);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Подтверждение регистрации BookCrossing");
        message.setText("Перейдите по ссылке, чтобы подтвердить создание аккаунта: "
                + String.format("https://localhost:%s/registration/confirmation?token=%s", port, token));

        emailSender.send(message);
    }
}
