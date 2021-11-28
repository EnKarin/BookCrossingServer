package ru.bookcrossing.BookcrossingServer.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@RequiredArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "t_role_id")
    private Role t_role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "t_user_id")
    private User t_user;

    public UserRole(Role r, User u){
        t_role = r;
        t_user = u;
    }

    public void setT_user(User t_user) {
        this.t_user = t_user;
    }

    public void setT_role(Role t_role) {
        this.t_role = t_role;
    }
}
