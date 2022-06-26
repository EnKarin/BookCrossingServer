package io.github.enkarin.bookcrossing.refresh.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "t_refresh")
public class Refresh {

    @Id
    private String refresh;

    private long date;

    private String user;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Refresh)) {
            return false;
        }
        final Refresh refreshObj = (Refresh) obj;
        return Objects.equals(refresh, refreshObj.refresh);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
