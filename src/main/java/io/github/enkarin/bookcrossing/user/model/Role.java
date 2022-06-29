package io.github.enkarin.bookcrossing.user.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@Table(name = "t_role")
public class Role implements GrantedAuthority {

    private static final long serialVersionUID = -6364597624261857651L;

    @Id
    private int roleId;

    private String name;

    @ManyToMany(mappedBy = "userRoles")
    @ToString.Exclude
    private Set<User> users;

    @Override
    public String getAuthority() {
        return getName();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Role)) {
            return false;
        }
        final Role role = (Role) obj;
        return Objects.equals(roleId, role.roleId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
