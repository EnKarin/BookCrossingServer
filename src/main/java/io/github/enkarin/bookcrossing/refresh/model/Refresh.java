package io.github.enkarin.bookcrossing.refresh.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "t_refresh")
public class Refresh {

    @Id
    @Column(name = "refresh_id", nullable = false)
    private String refreshId;

    @Column(name = "date", nullable = false)
    private long date;

    @Column(name = "r_user")
    private String user;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Refresh refreshObj)) {
            return false;
        }
        return Objects.equals(refreshId, refreshObj.refreshId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
