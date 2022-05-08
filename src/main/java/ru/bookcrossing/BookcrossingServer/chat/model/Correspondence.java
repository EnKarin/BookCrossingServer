package ru.bookcrossing.BookcrossingServer.chat.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "t_correspondence")
public class Correspondence {

    @EmbeddedId
    private UsersCorrKey usersCorrKey;

    @OneToMany(mappedBy = "correspondence")
    private List<Message> message;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Correspondence correspondence = (Correspondence) o;
        return Objects.equals(usersCorrKey, correspondence.usersCorrKey);
    }

}
