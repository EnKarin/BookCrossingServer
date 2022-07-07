package io.github.enkarin.bookcrossing.user.service;

import io.github.enkarin.bookcrossing.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public User loadUserByUsername(final String username) {
        return userService.findByLogin(username);
    }
}
