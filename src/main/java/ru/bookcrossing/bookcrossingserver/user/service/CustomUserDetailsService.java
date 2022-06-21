package ru.bookcrossing.bookcrossingserver.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import ru.bookcrossing.bookcrossingserver.user.model.User;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public User loadUserByUsername(final String username){
        return userService.findByLogin(username).orElseThrow();
    }
}
