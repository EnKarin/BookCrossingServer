package io.github.enkarin.bookcrossing.chat.model;

import io.github.enkarin.bookcrossing.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "t_messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_messages_gen")
    @SequenceGenerator(name = "t_messages_gen", sequenceName = "t_messages_seq", allocationSize = 1)
    @Column(name = "message_id", nullable = false)
    private long messageId;

    @ManyToOne
    @JoinColumn(name = "sender_user_id")
    private User sender;

    private String text;

    @Column(name = "departure_date", nullable = false)
    private long departureDate;

    @Column(name = "shown_first_user", nullable = false)
    private boolean shownFirstUser;

    @Column(name = "shown_second_user", nullable = false)
    private boolean shownSecondUser;

    private boolean declaim;

    @Column(name = "alert_sent", nullable = false)
    private boolean alertSent;

    @ManyToOne
    @JoinColumns(value = {
        @JoinColumn(name = "correspondence_first_user_id", referencedColumnName = "first_user_id"),
        @JoinColumn(name = "correspondence_second_user_id", referencedColumnName = "second_user_id")
    })
    private Correspondence correspondence;
}
