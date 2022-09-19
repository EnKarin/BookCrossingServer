package io.github.enkarin.bookcrossing.chat.model;

import io.github.enkarin.bookcrossing.user.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
    private User sender;

    private String text;

    private long departureDate;

    private boolean shownFirstUser;

    private boolean shownSecondUser;

    private boolean declaim;

    private boolean alertSent;

    @ManyToOne
    @JoinColumns({ // NOSONAR
        @JoinColumn(name = "correspondence_first_user_id", referencedColumnName = "first_user_id"),
        @JoinColumn(name = "correspondence_second_user_id", referencedColumnName = "second_user_id")
    })
    private Correspondence correspondence;
}
