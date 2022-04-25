package ru.bookcrossing.BookcrossingServer.approvemail.model;

import lombok.Getter;
import lombok.Setter;
import ru.bookcrossing.BookcrossingServer.approvemail.enums.ApproveType;
import ru.bookcrossing.BookcrossingServer.user.model.User;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "action_mail_user")
public class ActionMailUser {

    @Id
    private String confirmationMail;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    User user;

    @Enumerated
    private ApproveType type;
}
