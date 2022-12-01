package io.github.enkarin.bookcrossing.user.dto;

import io.github.enkarin.bookcrossing.user.model.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import javax.annotation.concurrent.Immutable;
import java.io.Serial;

@Immutable
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleDto implements GrantedAuthority {

    @Serial
    private static final long serialVersionUID = 7709992679902517401L;

    private final String name;

    @Override
    public String getAuthority() {
        return name;
    }

    public static RoleDto fromRole(final Role role) {
        return new RoleDto(role.getName());
    }
}
