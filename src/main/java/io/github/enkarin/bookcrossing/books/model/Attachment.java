package io.github.enkarin.bookcrossing.books.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
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
}
