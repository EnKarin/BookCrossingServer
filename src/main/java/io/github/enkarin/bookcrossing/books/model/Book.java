package io.github.enkarin.bookcrossing.books.model;

import io.github.enkarin.bookcrossing.books.enums.Status;
import io.github.enkarin.bookcrossing.user.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "t_book")
public class Book implements Serializable {

    @Serial
    private static final long serialVersionUID = -2338626292552177485L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int bookId;

    @NotNull
    private String title;

    private String author;

    @ManyToOne(optional = false)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    private String publishingHouse;

    private int year;

    @ManyToOne
    @JoinColumn(name = "owner_book")
    private User owner;

    @OneToOne(mappedBy = "book", orphanRemoval = true)
    private Attachment attachment;

    @ManyToMany(mappedBy = "bookmarks")
    private Set<User> usersBookmarks;

    @Enumerated(EnumType.STRING)
    private Status status;
}
