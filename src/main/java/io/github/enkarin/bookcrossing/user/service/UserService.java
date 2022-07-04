package io.github.enkarin.bookcrossing.user.service;

import io.github.enkarin.bookcrossing.exception.*;
import io.github.enkarin.bookcrossing.mail.model.ActionMailUser;
import io.github.enkarin.bookcrossing.mail.repository.ActionMailUserRepository;
import io.github.enkarin.bookcrossing.mail.service.MailService;
import io.github.enkarin.bookcrossing.refresh.service.RefreshService;
import io.github.enkarin.bookcrossing.registation.dto.AuthResponse;
import io.github.enkarin.bookcrossing.registation.dto.LoginRequest;
import io.github.enkarin.bookcrossing.registation.dto.UserDto;
import io.github.enkarin.bookcrossing.user.dto.UserDtoResponse;
import io.github.enkarin.bookcrossing.user.dto.UserProfileResponse;
import io.github.enkarin.bookcrossing.user.dto.UserPutRequest;
import io.github.enkarin.bookcrossing.user.model.Role;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.RoleRepository;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ActionMailUserRepository confirmationMailUserRepository;
    private final MailService mailService;
    private final RefreshService refreshService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;
    private TypeMap<UserDto, User> userDtoMapper;

    public User saveUser(final UserDto userDTO) {
        if (!userDTO.getPassword().equals(userDTO.getPasswordConfirm())) {
            throw new PasswordsDontMatchException();
        }
        if (userRepository.findByLogin(userDTO.getLogin()).isPresent()) {
            throw new LoginFailedException();
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailFailedException();
        }
        final User user = userRepository.save(convertToUser(userDTO));
        mailService.sendApproveMail(user);
        return user;
    }

    public AuthResponse confirmMail(final String token) {
        final ActionMailUser confirmationMailUser = confirmationMailUserRepository.findById(token)
                .orElseThrow(TokenNotFoundException::new);
        final User user = confirmationMailUser.getUser();
        user.setEnabled(true);
        confirmationMailUserRepository.delete(confirmationMailUser);
        userRepository.save(user);
        return refreshService.createTokens(user.getLogin());
    }

    public Optional<User> findByLogin(final String login) {
        return userRepository.findByLogin(login);
    }

    public Optional<User> findById(final int userId) {
        return userRepository.findById(userId);
    }

    public UserProfileResponse getProfile(final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        return modelMapper.map(user, UserProfileResponse.class);
    }

    public List<UserDtoResponse> findAllUsers(final int zone) {
        final Role role = roleRepository.getById(1);
        return userRepository.findByUserRoles(role).stream()
                .map(u -> new UserDtoResponse(u, zone))
                .collect(Collectors.toList());
    }

    public AuthResponse findByLoginAndPassword(final LoginRequest login) {
        final User user = userRepository.findByLogin(login.getLogin())
                .orElseThrow(UserNotFoundException::new);
        if (bCryptPasswordEncoder.matches(login.getPassword(), user.getPassword())) {
            if (user.isEnabled()) {
                if (user.isAccountNonLocked()) {
                    return refreshService.createTokens(login.getLogin());
                }
                throw new LockedAccountException();
            }
            throw new AccountNotConfirmedException();
        }
        throw new InvalidPasswordException();
    }

    public Optional<User> putUserInfo(final UserPutRequest userPutRequest, final String login) {
        Optional<User> user = findByLogin(login);
        if (user.isPresent()) {
            if (bCryptPasswordEncoder.matches(userPutRequest.getOldPassword(), user.get().getPassword())) {
                user.get().setName(userPutRequest.getName());
                user.get().setCity(userPutRequest.getCity());
                user.get().setPassword(bCryptPasswordEncoder.encode(userPutRequest.getNewPassword()));
                user = Optional.of(userRepository.save(user.get()));
                return user;
            } else {
                return Optional.empty();
            }
        } else {
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
        user.setUserRoles(Set.of(roleRepository.getById(1)));
        user.setAccountNonLocked(true);
        user.setEnabled(false);
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        return user;
    }
}
