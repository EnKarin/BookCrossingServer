package io.github.enkarin.bookcrossing.user.dto;

import io.github.enkarin.bookcrossing.user.model.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Immutable
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDetailsDto implements UserDetails {

    private static final long serialVersionUID = -5699584165911441893L;

    private final String login;

    private final String password;

    private final boolean accountNonLocked;

    private final boolean accountNonExpired;

    private final boolean credentialsNonExpired;

    private final boolean enabled;

    private final Set<RoleDto> roles;

    public static UserDetailsDto fromUser(final User user) {
        final Set<RoleDto> roles = user.getUserRoles().stream()
                .map(RoleDto::fromRole)
                .collect(Collectors.toUnmodifiableSet());
        return new UserDetailsDto(user.getLogin(), user.getPassword(), user.isAccountNonLocked(),
                user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isEnabled(), roles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
