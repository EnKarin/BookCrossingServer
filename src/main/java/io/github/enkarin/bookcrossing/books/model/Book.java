package io.github.enkarin.bookcrossing.books.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.enkarin.bookcrossing.user.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "t_book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int bookId;

    private String title;

    private String author;

    private String genre;

    private String publishingHouse;

    private int year;

    @ManyToOne
    @JoinColumn(name = "ownerBook")
    @JsonIgnore
    private User owner;

    @OneToOne
    @JoinColumn(name = "attachId")
    private Attachment attachment;

    @ManyToMany(mappedBy = "bookmarks")
    @JsonIgnore
    private Set<User> usersBookmarks;
}
