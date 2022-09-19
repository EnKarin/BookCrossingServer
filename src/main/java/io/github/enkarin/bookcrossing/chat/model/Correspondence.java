package io.github.enkarin.bookcrossing.chat.model;

import lombok.Getter;
import lombok.Setter;

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

    @OneToMany(mappedBy = "correspondence", orphanRemoval = true)
    private List<Message> message;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Correspondence)) {
            return false;
        }
        final Correspondence correspondence = (Correspondence) obj;
        return Objects.equals(usersCorrKey, correspondence.usersCorrKey);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
