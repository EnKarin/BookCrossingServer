package io.github.enkarin.bookcrossing.books.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Builder
@Table(name = "t_genre")
@EqualsAndHashCode
@ToString
public class Genre {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "ru_name")
    private String ruName;

    @Column(name = "eng_name")
    private String engName;
}
