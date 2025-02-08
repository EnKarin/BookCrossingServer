package io.github.enkarin.bookcrossing.books.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
    @GeneratedValue
    private int attachId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "attach_id")
    private Book book;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(length = 5_242_880, name = "data")
    private byte[] originalImage;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(length = 5_242_880)
    private byte[] listImage;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(length = 512_000)
    private byte[] thumbImage;

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
