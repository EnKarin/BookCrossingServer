package ru.bookcrossing.BookcrossingServer.chat.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "t_correspondence")
public class Correspondence {

    @EmbeddedId
    private UsersCorrKey usersCorrKey;

    @OneToMany(mappedBy = "correspondence")
    private List<Message> message;

}
