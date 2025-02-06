package io.github.enkarin.bookcrossing.books.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Builder
@Table(name = "t_genre")
@EqualsAndHashCode
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Genre implements Serializable {
    @Serial
    private static final long serialVersionUID = 0;

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "ru_name")
    private String ruName;

    @Column(name = "eng_name")
    private String engName;

    @OneToMany(mappedBy = "genre")
    private List<Book> book;
}
