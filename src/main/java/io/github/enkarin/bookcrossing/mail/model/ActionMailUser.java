package io.github.enkarin.bookcrossing.mail.model;

import io.github.enkarin.bookcrossing.mail.enums.ApproveType;
import io.github.enkarin.bookcrossing.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "t_action_mail_user")
public class ActionMailUser {

    @Id
    @Column(name = "confirmation_mail", nullable = false)
    private String confirmationMail;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated
    @Column(name = "type")
    private ApproveType type;
}
