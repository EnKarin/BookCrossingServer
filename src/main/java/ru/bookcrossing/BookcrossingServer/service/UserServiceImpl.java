package ru.bookcrossing.BookcrossingServer.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.entity.Role;
import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.model.DTO.UserDTO;
import ru.bookcrossing.BookcrossingServer.model.request.LoginRequest;
import ru.bookcrossing.BookcrossingServer.model.request.UserPutRequest;
import ru.bookcrossing.BookcrossingServer.repository.BookRepository;
import ru.bookcrossing.BookcrossingServer.repository.RoleRepository;
import ru.bookcrossing.BookcrossingServer.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;
    private TypeMap<UserDTO, User> userDtoMapper = null;

    @Override
    public Optional<String> saveUser(UserDTO userDTO){
        if (userRepository.findByLogin(userDTO.getLogin()) != null) {
            return Optional.of("login: Пользователь с таким логином уже существует");
        }
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            return Optional.of("email: Пользователь с таким почтовым адресом уже существует");
        }
        User user = convertToUser(userDTO);
        userRepository.save(user);
        return Optional.empty();
    }

    @Transactional
    @Override
    public void deleteUser(String login) {
        User user = userRepository.findByLogin(login);
        bookRepository.deleteByOwner(user);
        userRepository.deleteByLogin(login);
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
    public Optional<User> putUserInfo(UserPutRequest userPutRequest, String login){
        Optional<User> user = findByLogin(login);
        if (bCryptPasswordEncoder.matches(userPutRequest.getOldPassword(), user.get().getPassword())) {
            User check = userRepository.findByEmail(userPutRequest.getEmail());
            if(login.equals(check.getLogin())) {
                user.get().setEmail(userPutRequest.getEmail());
                user.get().setName(userPutRequest.getName());
                user.get().setCity(userPutRequest.getCity());
                user.get().setPassword(bCryptPasswordEncoder.encode(userPutRequest.getNewPassword()));
                user = Optional.of(userRepository.save(user.get()));
            }
            else return Optional.empty();
            return user;
        } else throw  new IllegalArgumentException();
    }

    private User convertToUser(UserDTO userDTO){
        if(userDtoMapper == null){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
            userDtoMapper = modelMapper.createTypeMap(UserDTO.class, User.class);
            userDtoMapper.addMappings(ms -> {
                ms.skip(User::setUserRoles);
                ms.skip(User::setAccountNonLocked);
                ms.skip(User::setPassword);
            });
        }
        User user = modelMapper.map(userDTO, User.class);
        user.setUserRoles(Collections.singleton(roleRepository.getById(1)));
        user.setAccountNonLocked(true);
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        return user;
    }
}
