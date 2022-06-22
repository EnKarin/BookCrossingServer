package io.github.enkarin.bookcrossing.books.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_attach")
public class Attachment {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String name;

    @Lob
    @Column(length = 3_145_728)
    private byte[] data;

    private String expansion;
}
