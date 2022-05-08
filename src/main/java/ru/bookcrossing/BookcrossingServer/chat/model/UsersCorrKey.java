package ru.bookcrossing.BookcrossingServer.chat.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import ru.bookcrossing.BookcrossingServer.user.model.User;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UsersCorrKey usersCorrKey = (UsersCorrKey) o;
        return Objects.equals(firstUser, usersCorrKey.firstUser)
                && Objects.equals(secondUser, usersCorrKey.secondUser);
    }
}
