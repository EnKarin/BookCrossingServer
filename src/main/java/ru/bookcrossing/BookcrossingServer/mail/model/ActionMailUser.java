package ru.bookcrossing.BookcrossingServer.mail.model;

import lombok.Getter;
import lombok.Setter;
import ru.bookcrossing.BookcrossingServer.mail.enums.ApproveType;
import ru.bookcrossing.BookcrossingServer.user.model.User;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "t_action_mail_user")
public class ActionMailUser {

    @Id
    private String confirmationMail;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    User user;

    @Enumerated
    private ApproveType type;
}
