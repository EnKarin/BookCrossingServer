package ru.bookcrossing.BookcrossingServer.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Refresh {

    @Id
    private String refresh;

    private long date;

    private String user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Refresh refresh1 = (Refresh) o;
        return refresh != null && Objects.equals(refresh, refresh1.refresh);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
