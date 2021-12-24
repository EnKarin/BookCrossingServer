package ru.bookcrossing.BookcrossingServer.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "t_book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String title;

    private String author;

    private String genre;

    private String publishingHouse;

    private int year;

    @ManyToOne
    @JoinColumn(name = "ownerBook")
    @ToString.Exclude
    private User owner;
}
