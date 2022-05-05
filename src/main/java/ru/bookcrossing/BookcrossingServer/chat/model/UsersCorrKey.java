package ru.bookcrossing.BookcrossingServer.chat.model;

import lombok.Getter;
import lombok.Setter;
import ru.bookcrossing.BookcrossingServer.user.model.User;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class UsersCorrKey implements Serializable {

    private static final long serialVersionUID = -4320535988539224004L;

    @ManyToOne
    @JoinColumn(name = "first_user_id")
    private User firstUser;

    @ManyToOne
    @JoinColumn(name = "second_user_id")
    private User secondUser;
}
