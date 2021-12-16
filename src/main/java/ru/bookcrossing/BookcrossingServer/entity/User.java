package ru.bookcrossing.BookcrossingServer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Имя должно содержать хотя бы один видимый символ")
    private String name;

    @NotBlank(message = "Логин должен содержать хотя бы один видимый символ")
    private String login;

    @NotBlank(message = "Пароль должен содержать хотя бы один видимый символ")
    @Size(min = 6, message = "Пароль должен содержать больше 6 символов")
    private String password;

    @Transient
    private String passwordConfirm;

    @Email(message = "Некорректный почтовый адрес")
    private String email;

    private String city;

    @OneToMany(mappedBy = "t_user", fetch = FetchType.EAGER)
    @ToString.Exclude
    @JsonIgnore
    private Set<UserRole> userRoles;

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
