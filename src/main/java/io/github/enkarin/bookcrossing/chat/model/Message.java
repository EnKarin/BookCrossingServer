package io.github.enkarin.bookcrossing.chat.model;

import io.github.enkarin.bookcrossing.user.model.User;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
