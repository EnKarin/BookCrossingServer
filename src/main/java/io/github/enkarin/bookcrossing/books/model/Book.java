package io.github.enkarin.bookcrossing.books.model;

import io.github.enkarin.bookcrossing.user.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "t_book")
public class Book implements Serializable {

    private static final long serialVersionUID = -2338626292552177485L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int bookId;

    @NotNull
    private String title;

    private String author;

    private String genre;

    private String publishingHouse;

    private int year;

    @ManyToOne
    @JoinColumn(name = "ownerBook")
    private User owner;

    @OneToOne
    @JoinColumn(name = "attachId")
    private Attachment attachment;

    @ManyToMany(mappedBy = "bookmarks")
    private Set<User> usersBookmarks;
}
