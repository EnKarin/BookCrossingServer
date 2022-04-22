package ru.bookcrossing.BookcrossingServer.mail.model;

import lombok.Getter;
import lombok.Setter;
import ru.bookcrossing.BookcrossingServer.user.model.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
public class ConfirmationMailUser {

    @Id
    private String confirmationMail;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    User user;
}
