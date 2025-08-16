package io.github.enkarin.bookcrossing.books.model;

import io.github.enkarin.bookcrossing.user.model.User;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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

    private String genre;

    private String publishingHouse;

    private int year;

    @ManyToOne
    @JoinColumn(name = "owner_book")
    private User owner;

    @OneToOne(mappedBy = "book", orphanRemoval = true)
    private Attachment attachment;

    @ManyToMany(mappedBy = "bookmarks")
    private Set<User> usersBookmarks;
}
