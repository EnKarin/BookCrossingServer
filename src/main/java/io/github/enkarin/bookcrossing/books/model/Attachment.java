package io.github.enkarin.bookcrossing.books.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_attach")
public class Attachment implements Serializable {

    @Serial
    private static final long serialVersionUID = 4600249981575739954L;

    @Id
    @Column(name = "attach_id", nullable = false)
    private int attachId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "attach_id")
    private Book book;

    @Column(length = 3_145_728)
    private byte[] data;

    private String expansion;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof final Attachment that)) {
            return false;
        }
        return Objects.equals(attachId, that.attachId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
