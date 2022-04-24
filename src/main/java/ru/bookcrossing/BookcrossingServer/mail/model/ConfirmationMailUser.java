package ru.bookcrossing.BookcrossingServer.mail.model;

import lombok.Getter;
import lombok.Setter;
import ru.bookcrossing.BookcrossingServer.user.model.User;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "confirmation_mail_user")
public class ConfirmationMailUser {

    @Id
    private String confirmationMail;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    User user;
}
