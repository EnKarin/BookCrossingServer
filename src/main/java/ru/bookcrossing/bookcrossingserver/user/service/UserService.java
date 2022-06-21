package ru.bookcrossing.bookcrossingserver.user.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bookcrossing.bookcrossingserver.exception.EmailFailedException;
import ru.bookcrossing.bookcrossingserver.exception.LoginFailedException;
import ru.bookcrossing.bookcrossingserver.exception.UserNotFoundException;
import ru.bookcrossing.bookcrossingserver.mail.model.ActionMailUser;
import ru.bookcrossing.bookcrossingserver.mail.repository.ActionMailUserRepository;
import ru.bookcrossing.bookcrossingserver.registation.dto.LoginRequest;
import ru.bookcrossing.bookcrossingserver.registation.dto.UserDto;
import ru.bookcrossing.bookcrossingserver.user.dto.UserDtoResponse;
import ru.bookcrossing.bookcrossingserver.user.dto.UserProfileResponse;
import ru.bookcrossing.bookcrossingserver.user.dto.UserPutRequest;
import ru.bookcrossing.bookcrossingserver.user.model.Role;
import ru.bookcrossing.bookcrossingserver.user.model.User;
import ru.bookcrossing.bookcrossingserver.user.repository.RoleRepository;
import ru.bookcrossing.bookcrossingserver.user.repository.UserRepository;

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
    private TypeMap<UserDto, User> userDtoMapper;

    public User saveUser(final UserDto userDTO) {
        if (userRepository.findByLogin(userDTO.getLogin()).isPresent()) {
            throw new LoginFailedException();
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailFailedException();
        }
        final User user = convertToUser(userDTO);
        return userRepository.save(user);
    }

    public Optional<String> confirmMail(final String token) {
        final Optional<ActionMailUser> confirmationMailUser = confirmationMailUserRepository.findById(token);
        if (confirmationMailUser.isPresent()) {
            final User user = confirmationMailUser.get().getUser();
            user.setEnabled(true);
            confirmationMailUserRepository.delete(confirmationMailUser.get());
            userRepository.save(user);
            return Optional.of(user.getLogin());
        } else {
            return Optional.empty();
        }
    }

    public Optional<User> findByLogin(final String login) {
        return userRepository.findByLogin(login);
    }

    public Optional<User> findById(final int userId) {
        return userRepository.findById(userId);
    }

    public UserProfileResponse getProfile(final String login){
        final User user = userRepository.findByLogin(login).orElseThrow();
        return modelMapper.map(user, UserProfileResponse.class);
    }

    public List<UserDtoResponse> findAllUsers(final int zone) {
        final Role role = roleRepository.getById(1);
        return userRepository.findByUserRoles(role).stream()
                .map(u -> new UserDtoResponse(u, zone))
                .collect(Collectors.toList());
    }

    public Optional<User> findByLoginAndPassword(final LoginRequest login){
        final Optional<User> user = userRepository.findByLogin(login.getLogin());
        if (user.isPresent() && bCryptPasswordEncoder.matches(login.getPassword(), user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    public Optional<User> putUserInfo(final UserPutRequest userPutRequest, final String login) {
        Optional<User> user = findByLogin(login);
        if(user.isPresent()) {
            if (bCryptPasswordEncoder.matches(userPutRequest.getOldPassword(), user.get().getPassword())) {
                user.get().setName(userPutRequest.getName());
                user.get().setCity(userPutRequest.getCity());
                user.get().setPassword(bCryptPasswordEncoder.encode(userPutRequest.getNewPassword()));
                user = Optional.of(userRepository.save(user.get()));
                return user;
            } else {
                return Optional.empty();
            }
        }
        else {
            throw new UserNotFoundException();
        }
    }


    private User convertToUser(final UserDto userDTO) {
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
        final User user = modelMapper.map(userDTO, User.class);
        user.setUserRoles(Collections.singleton(roleRepository.getById(1)));
        user.setAccountNonLocked(true);
        user.setEnabled(false);
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        return user;
    }
}
