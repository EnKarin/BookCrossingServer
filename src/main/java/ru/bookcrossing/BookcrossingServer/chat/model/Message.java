package ru.bookcrossing.BookcrossingServer.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.bookcrossing.BookcrossingServer.user.model.User;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "t_message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long messageId;

    @ManyToOne
    @JoinColumn(name = "sender_user_id")
    private User sender;

    private String text;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "correspondence_first_user_id", referencedColumnName = "first_user_id"),
            @JoinColumn(name = "correspondence_second_user_id", referencedColumnName = "second_user_id")
    })
    @JsonIgnore
    private Correspondence correspondence;
}
