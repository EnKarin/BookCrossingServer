package io.github.enkarin.bookcrossing.user.model;

import io.github.enkarin.bookcrossing.books.model.Book;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serial;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "t_user")
public class User implements UserDetails {

    @Serial
    private static final long serialVersionUID = -6289855612211696141L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;

    private String name;

    private String login;

    private String password;

    private String email;

    private String city;

    private boolean accountNonLocked;

    private boolean enabled;

    private long loginDate;

    @ManyToMany
    @JoinTable(
        name = "t_user_role",
        joinColumns = {@JoinColumn(name = "user_id")},
        inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> userRoles;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Book> books;

    @ManyToMany
    @JoinTable(
        name = "t_bookmarks",
        joinColumns = {@JoinColumn(name = "user_id")},
        inverseJoinColumns = {@JoinColumn(name = "book_id")}
    )
    private Set<Book> bookmarks;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRoles;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof final User user)) {
            return false;
        }
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
