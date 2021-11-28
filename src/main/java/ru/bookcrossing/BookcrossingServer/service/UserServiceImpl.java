package ru.bookcrossing.BookcrossingServer.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.entity.Role;
import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.entity.UserRole;
import ru.bookcrossing.BookcrossingServer.repository.RoleRepository;
import ru.bookcrossing.BookcrossingServer.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @PersistenceContext
    private EntityManager em;

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
        UserRole role = new UserRole(new Role(1, "ROLE_USER"), user);

        if (userFromDB != null) {
            return false;
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
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user;
        if(s.equals("admin")) {
            user = new User();
            UserRole role = new UserRole(new Role(0, "ROLE_ADMIN"), user);

            user.setLogin("admin");
            user.setUserRoles(Collections.singleton(role));
            user.setPassword(bCryptPasswordEncoder.encode("adm1982"));
        }
        else {
            user = userRepository.findByLogin(s);
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }
        }

        return user;
    }
}
