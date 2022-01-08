package ru.bookcrossing.BookcrossingServer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.config.jwt.JwtProvider;
import ru.bookcrossing.BookcrossingServer.entity.Role;
import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.model.request.LoginRequest;
import ru.bookcrossing.BookcrossingServer.model.DTO.UserDTO;
import ru.bookcrossing.BookcrossingServer.model.request.UserPutRequest;
import ru.bookcrossing.BookcrossingServer.repository.RoleRepository;
import ru.bookcrossing.BookcrossingServer.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public boolean saveUser(UserDTO userDTO){
        Role role;

        if (userRepository.findByLogin(userDTO.getLogin()) != null) {
            return false;
        }
        
        User user = new User();
        user.setName(userDTO.getName());
        user.setLogin(userDTO.getLogin());
        user.setCity(userDTO.getCity());
        user.setEmail(userDTO.getEmail());

        if(userDTO.getLogin().equals("admin")) {
            role = roleRepository.getById(0);
        }
        else {
            role = roleRepository.getById(1);
        }
        user.setUserRoles(Collections.singleton(role));
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    public void deleteUser(Integer userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return Optional.of(userRepository.findByLogin(login));
    }

    @Override
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAllUsers() {
        Role role = roleRepository.getById(1);
        return userRepository.findByUserRoles(role);
    }

    @Override
    public Optional<User> findByLoginAndPassword(LoginRequest login) throws UsernameNotFoundException {
        Optional<User> user = Optional.of(userRepository.findByLogin(login.getLogin()));
        if(bCryptPasswordEncoder.matches(login.getPassword(), user.get().getPassword())){
            return user;
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> putUserInfo(UserPutRequest userPutRequest){
        Optional<User> user = findByLogin(jwtProvider.getLoginFromToken());
        if(user.isPresent()) {
            if (bCryptPasswordEncoder.matches(userPutRequest.getOldPassword(), user.get().getPassword())) {
                user.get().setName(userPutRequest.getName());
                user.get().setCity(userPutRequest.getCity());
                user.get().setEmail(userPutRequest.getEmail());
                user.get().setPassword(bCryptPasswordEncoder.encode(userPutRequest.getNewPassword()));
                user = Optional.of(userRepository.save(user.get()));
                return user;
            }
        }
        return Optional.empty();
    }
}
