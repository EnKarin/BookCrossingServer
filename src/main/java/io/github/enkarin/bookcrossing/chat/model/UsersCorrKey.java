package io.github.enkarin.bookcrossing.chat.model;

import io.github.enkarin.bookcrossing.user.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class UsersCorrKey implements Serializable {

    @Serial
    private static final long serialVersionUID = -4320535988539224004L;

    @ManyToOne
    @JoinColumn(name = "first_user_id")
    private User firstUser;

    @ManyToOne
    @JoinColumn(name = "second_user_id")
    private User secondUser;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof final UsersCorrKey usersCorrKey)) {
            return false;
        }
        return Objects.equals(firstUser, usersCorrKey.firstUser) && Objects.equals(secondUser, usersCorrKey.secondUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstUser, secondUser);
    }
}
