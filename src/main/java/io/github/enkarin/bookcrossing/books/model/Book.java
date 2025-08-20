package io.github.enkarin.bookcrossing.books.model;

import io.github.enkarin.bookcrossing.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_book_gen")
    @SequenceGenerator(name = "t_book_gen", sequenceName = "t_book_seq", allocationSize = 1)
    @Column(name = "book_id", nullable = false)
    private int bookId;

    @NotNull
    private String title;

    private String author;

    private String genre;

    @Column(name = "publishing_house")
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
