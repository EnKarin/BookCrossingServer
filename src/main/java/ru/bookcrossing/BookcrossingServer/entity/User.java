package ru.bookcrossing.BookcrossingServer.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Validated
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "t_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank
    private String name;

    @NotBlank
    private String login;

    @NotBlank
    @Size(min = 6, message = "Пароль должен содержать больше 6 символов")
    private String password;

    @Transient
    private String passwordConfirm;

    @Email
    private String email;

    private String city;

    @OneToMany(mappedBy = "t_user")
    @ToString.Exclude
    private Set<UserRole> userRoles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getUserRoles().stream().map(UserRole::getT_role).collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
