package io.github.enkarin.bookcrossing.books.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_attach")
public class Attachment implements Serializable {

    private static final long serialVersionUID = 4600249981575739954L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String name;

    @Lob
    @Column(length = 3_145_728)
    private byte[] data;

    private String expansion;

    @OneToOne(mappedBy = "attachment")
    private Book book;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Attachment)) {
            return false;
        }
        final Attachment that = (Attachment) obj;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
