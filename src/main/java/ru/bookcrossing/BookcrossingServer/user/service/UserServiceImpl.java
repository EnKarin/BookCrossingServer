package ru.bookcrossing.BookcrossingServer.user.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.exception.EmailFailedException;
import ru.bookcrossing.BookcrossingServer.exception.LoginFailedException;
import ru.bookcrossing.BookcrossingServer.mail.model.ConfirmationMailUser;
import ru.bookcrossing.BookcrossingServer.mail.repository.ConfirmationMailUserRepository;
import ru.bookcrossing.BookcrossingServer.registation.request.LoginRequest;
import ru.bookcrossing.BookcrossingServer.user.dto.UserDto;
import ru.bookcrossing.BookcrossingServer.user.model.Role;
import ru.bookcrossing.BookcrossingServer.user.model.User;
import ru.bookcrossing.BookcrossingServer.user.repository.RoleRepository;
import ru.bookcrossing.BookcrossingServer.user.repository.UserRepository;
import ru.bookcrossing.BookcrossingServer.user.request.UserPutRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ConfirmationMailUserRepository confirmationMailUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;
    private TypeMap<UserDto, User> userDtoMapper = null;

    @Override
    public User saveUser(UserDto userDTO){
        if (userRepository.findByLogin(userDTO.getLogin()) != null) {
            throw new LoginFailedException();
        }
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new EmailFailedException();
        }
        User user = convertToUser(userDTO);
        return userRepository.save(user);
    }

    @Override
    public boolean confirmMail(String token){
        Optional<ConfirmationMailUser> confirmationMailUser = confirmationMailUserRepository.findById(token);
        if(confirmationMailUser.isPresent()){
            User user = confirmationMailUser.get().getUser();
            user.setEnabled(true);
            confirmationMailUserRepository.delete(confirmationMailUser.get());
            return true;
        }
        else return false;
    }

    @Override
    public boolean lockedUser(String login) {
        Optional<User> user = Optional.ofNullable(userRepository.findByLogin(login));
        if(user.isPresent()) {
            user.get().setAccountNonLocked(false);
            userRepository.save(user.get());
            return true;
        }
        else return false;
    }

    @Override
    public boolean nonLockedUser(String login) {
        Optional<User> user = Optional.ofNullable(userRepository.findByLogin(login));
        if(user.isPresent()) {
            user.get().setAccountNonLocked(true);
            userRepository.save(user.get());
            return true;
        }
        else return false;
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
        Optional<User> user = Optional.ofNullable(userRepository.findByLogin(login.getLogin()));
        if(user.isPresent() && bCryptPasswordEncoder.matches(login.getPassword(), user.get().getPassword())){
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

    private User convertToUser(UserDto userDTO){
        if(userDtoMapper == null){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
            userDtoMapper = modelMapper.createTypeMap(UserDto.class, User.class);
            userDtoMapper.addMappings(ms -> {
                ms.skip(User::setUserRoles);
                ms.skip(User::setAccountNonLocked);
                ms.skip(User::setPassword);
                ms.skip(User::setEnabled);
            });
        }
        User user = modelMapper.map(userDTO, User.class);
        user.setUserRoles(Collections.singleton(roleRepository.getById(1)));
        user.setAccountNonLocked(true);
        user.setEnabled(false);
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        return user;
    }
}
