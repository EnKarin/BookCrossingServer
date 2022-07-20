package io.github.enkarin.bookcrossing.refresh.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "t_refresh")
public class Refresh {

    @Id
    private String refreshId;

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
        return Objects.equals(refreshId, refreshObj.refreshId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
