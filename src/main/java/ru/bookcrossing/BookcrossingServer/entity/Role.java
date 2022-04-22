package ru.bookcrossing.BookcrossingServer.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "t_role")
public class Role implements GrantedAuthority {

    @Id
    private int role_id;

    private String name;

    @ManyToMany(mappedBy = "userRoles")
    @ToString.Exclude
    private Set<User> users;

    @Override
    public String getAuthority() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Role role = (Role) o;
        return Objects.equals(role_id, role.role_id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
