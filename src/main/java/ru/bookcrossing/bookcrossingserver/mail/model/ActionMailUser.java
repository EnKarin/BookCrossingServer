package ru.bookcrossing.bookcrossingserver.mail.model;

import lombok.Getter;
import lombok.Setter;
import ru.bookcrossing.bookcrossingserver.mail.enums.ApproveType;
import ru.bookcrossing.bookcrossingserver.user.model.User;

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
    private User user;

    @Enumerated
    private ApproveType type;
}
