package ru.bookcrossing.BookcrossingServer.books.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "attach")
public class Attachment {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String name;

    @Lob
    private byte[] data;

    private String expansion;
}
