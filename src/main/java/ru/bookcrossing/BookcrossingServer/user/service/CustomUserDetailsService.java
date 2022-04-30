package ru.bookcrossing.BookcrossingServer.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.bookcrossing.BookcrossingServer.user.model.User;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private UserService userService;

    @Autowired
    private void setUserService(UserService service){
        userService = service;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.findByLogin(username).orElseThrow();
    }
}
