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
import ru.bookcrossing.BookcrossingServer.exception.UserNotFoundException;
import ru.bookcrossing.BookcrossingServer.mail.model.ActionMailUser;
import ru.bookcrossing.BookcrossingServer.mail.repository.ActionMailUserRepository;
import ru.bookcrossing.BookcrossingServer.registation.dto.LoginRequest;
import ru.bookcrossing.BookcrossingServer.user.dto.UserDto;
import ru.bookcrossing.BookcrossingServer.user.dto.UserDtoResponse;
import ru.bookcrossing.BookcrossingServer.user.dto.UserProfileResponse;
import ru.bookcrossing.BookcrossingServer.user.dto.UserPutRequest;
import ru.bookcrossing.BookcrossingServer.user.model.Role;
import ru.bookcrossing.BookcrossingServer.user.model.User;
import ru.bookcrossing.BookcrossingServer.user.repository.RoleRepository;
import ru.bookcrossing.BookcrossingServer.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ActionMailUserRepository confirmationMailUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;
    private TypeMap<UserDto, User> userDtoMapper = null;

    public User saveUser(UserDto userDTO) {
        if (userRepository.findByLogin(userDTO.getLogin()).isPresent()) {
            throw new LoginFailedException();
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailFailedException();
        }
        User user = convertToUser(userDTO);
        return userRepository.save(user);
    }

    public boolean confirmMail(String token) {
        Optional<ActionMailUser> confirmationMailUser = confirmationMailUserRepository.findById(token);
        if (confirmationMailUser.isPresent()) {
            User user = confirmationMailUser.get().getUser();
            user.setEnabled(true);
            confirmationMailUserRepository.delete(confirmationMailUser.get());
            userRepository.save(user);
            return true;
        } else return false;
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    public UserProfileResponse getProfile(String login){
        User user = userRepository.findByLogin(login).orElseThrow();
        return modelMapper.map(user, UserProfileResponse.class);
    }

    public List<UserDtoResponse> findAllUsers(int zone) {
        Role role = roleRepository.getById(1);
        return userRepository.findByUserRoles(role).stream()
                .map(u -> new UserDtoResponse(u, zone))
                .collect(Collectors.toList());
    }

    public Optional<User> findByLoginAndPassword(LoginRequest login) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByLogin(login.getLogin());
        if (user.isPresent() && bCryptPasswordEncoder.matches(login.getPassword(), user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    public Optional<User> putUserInfo(UserPutRequest userPutRequest, String login) {
        Optional<User> user = findByLogin(login);
        if(user.isPresent()) {
            if (bCryptPasswordEncoder.matches(userPutRequest.getOldPassword(), user.get().getPassword())) {
                user.get().setName(userPutRequest.getName());
                user.get().setCity(userPutRequest.getCity());
                user.get().setPassword(bCryptPasswordEncoder.encode(userPutRequest.getNewPassword()));
                user = Optional.of(userRepository.save(user.get()));
                return user;
            } else return Optional.empty();
        }
        else throw new UserNotFoundException();
    }


    private User convertToUser(UserDto userDTO) {
        if (userDtoMapper == null) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
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
