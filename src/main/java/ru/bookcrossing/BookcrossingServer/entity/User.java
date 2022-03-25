package ru.bookcrossing.BookcrossingServer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "t_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int user_id;

    private String name;

    private String login;

    @ToString.Exclude
    @JsonIgnore
    private String password;

    private String email;

    private String city;

    private boolean accountNonLocked;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.REFRESH,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "User_Role",
            joinColumns = { @JoinColumn(name = "user_id")},
            inverseJoinColumns = { @JoinColumn(name = "role_id")}
    )
    @ToString.Exclude
    @JsonIgnore
    private Set<Role> userRoles;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<Book> books;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return Objects.equals(user_id, user.user_id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRoles;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
