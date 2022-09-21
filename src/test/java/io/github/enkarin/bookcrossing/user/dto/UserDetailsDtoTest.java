package io.github.enkarin.bookcrossing.user.dto;

import io.github.enkarin.bookcrossing.user.model.Role;
import io.github.enkarin.bookcrossing.user.model.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsDtoTest {

    @Test
    void fromUserTest() {
        final User user = new User();
        user.setLogin("login");
        user.setPassword("pass");
        user.setAccountNonLocked(false);
        user.setEnabled(false);
        user.setUserRoles(Set.of(new Role()));

        final UserDetailsDto userDetailsDto = UserDetailsDto.fromUser(user);
        assertThat(userDetailsDto)
                .isNotNull()
                .satisfies(u -> {
                    assertThat(u.getLogin())
                            .isEqualTo("login");
                    assertThat(u.getPassword())
                            .isEqualTo("pass");
                    assertThat(u.isAccountNonLocked())
                            .isFalse();
                    assertThat(u.isEnabled())
                            .isFalse();
                    assertThat(u.isAccountNonExpired())
                            .isTrue();
                    assertThat(u.isCredentialsNonExpired())
                            .isTrue();
                    assertThat(u.getRoles())
                            .hasSize(1)
                            .isUnmodifiable();
                });
    }
}
