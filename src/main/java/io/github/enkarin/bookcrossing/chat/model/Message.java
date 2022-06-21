package io.github.enkarin.bookcrossing.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.enkarin.bookcrossing.user.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "t_messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long messageId;

    @ManyToOne
    @JoinColumn(name = "sender_user_id")
    @JsonIgnore
    private User sender;

    private String text;

    @JsonIgnore
    private long departureDate;

    @JsonIgnore
    private boolean shownFirstUser;

    @JsonIgnore
    private boolean shownSecondUser;

    private boolean declaim;

    @JsonIgnore
    private boolean alertSent;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "correspondence_first_user_id", referencedColumnName = "first_user_id"),
            @JoinColumn(name = "correspondence_second_user_id", referencedColumnName = "second_user_id")
    })
    @JsonIgnore
    private Correspondence correspondence;
}
