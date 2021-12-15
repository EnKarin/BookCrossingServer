package ru.bookcrossing.BookcrossingServer.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.entity.Role;
import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.entity.UserRole;
import ru.bookcrossing.BookcrossingServer.model.Login;
import ru.bookcrossing.BookcrossingServer.repository.RoleRepository;
import ru.bookcrossing.BookcrossingServer.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository usersRepository,
                           RoleRepository roleRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = usersRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public boolean saveUser(User user){
        User userFromDB = userRepository.findByLogin(user.getLogin());
        UserRole role;

        if (userFromDB != null) {
            return false;
        }

        if(user.getLogin().equals("admin")) {
            role = new UserRole(new Role(0, "ROLE_ADMIN"), user);
        }
        else {
            role = new UserRole(new Role(1, "ROLE_USER"), user);
        }
        user.setUserRoles(Collections.singleton(role));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        roleRepository.save(role);
        return true;
    }

    @Override
    public void deleteUser(Integer userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
        }
    }

    @Override
    public User findByLogin(String login) {
        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByLoginAndPassword(Login login) throws UsernameNotFoundException {
        User user;
            user = userRepository.findByLogin(login.getUsername());
            if (user != null) {
                if(bCryptPasswordEncoder.matches(login.getPassword(), user.getPassword())){
                    return user;
                }
            }
            else{
                throw new UsernameNotFoundException("User not found");
            }

        return null;
    }
}
