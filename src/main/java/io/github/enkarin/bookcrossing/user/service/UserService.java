package io.github.enkarin.bookcrossing.user.service;

import io.github.enkarin.bookcrossing.exception.*;
import io.github.enkarin.bookcrossing.mail.model.ActionMailUser;
import io.github.enkarin.bookcrossing.mail.repository.ActionMailUserRepository;
import io.github.enkarin.bookcrossing.mail.service.MailService;
import io.github.enkarin.bookcrossing.refresh.service.RefreshService;
import io.github.enkarin.bookcrossing.registration.dto.AuthResponse;
import io.github.enkarin.bookcrossing.registration.dto.LoginRequest;
import io.github.enkarin.bookcrossing.registration.dto.UserRegistrationDto;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import io.github.enkarin.bookcrossing.user.dto.UserProfileDto;
import io.github.enkarin.bookcrossing.user.dto.UserPublicProfileDto;
import io.github.enkarin.bookcrossing.user.dto.UserPutProfileDto;
import io.github.enkarin.bookcrossing.user.model.Role;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.RoleRepository;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ActionMailUserRepository confirmationMailUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MailService mailService;
    private final RefreshService refreshService;

    @Transactional
    public UserDto saveUser(final UserRegistrationDto userRegistrationDTO) {
        if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getPasswordConfirm())) {
            throw new PasswordsDontMatchException();
        }
        if (userRepository.findByLogin(userRegistrationDTO.getLogin()).isPresent()) {
            throw new LoginFailedException();
        }
        if (userRepository.findByEmail(userRegistrationDTO.getEmail()).isPresent()) {
            throw new EmailFailedException();
        }
        final User user = userRepository.save(convertToUser(userRegistrationDTO));
        mailService.sendApproveMail(user);
        return UserDto.fromUser(user);
    }

    @Transactional
    public AuthResponse confirmMail(final String token) {
        final ActionMailUser confirmationMailUser = confirmationMailUserRepository.findById(token)
                .orElseThrow(TokenNotFoundException::new);
        final User user = confirmationMailUser.getUser();
        user.setEnabled(true);
        confirmationMailUserRepository.delete(confirmationMailUser);
        userRepository.save(user);
        return refreshService.createTokens(user.getLogin());
    }

    public User findByLogin(final String login) {
        return userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
    }

    public UserPublicProfileDto findById(final int userId, final int zone) {
        return UserPublicProfileDto.fromUser(userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new), zone);
    }

    public UserProfileDto getProfile(final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        return UserProfileDto.fromUser(user);
    }

    public List<UserPublicProfileDto> findAllUsers(final int zone) {
        final Role role = roleRepository.getRoleByName("ROLE_USER");
        return userRepository.findByUserRoles(role).stream()
                .map(u -> UserPublicProfileDto.fromUser(u, zone))
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

    @Transactional
    public UserProfileDto putUserInfo(final UserPutProfileDto userPutProfileDto, final String login) {
        if (!userPutProfileDto.getNewPassword().equals(userPutProfileDto.getPasswordConfirm())) {
            throw new PasswordsDontMatchException();
        }
        User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        if (bCryptPasswordEncoder.matches(userPutProfileDto.getOldPassword(), user.getPassword())) {
            user.setName(userPutProfileDto.getName());
            user.setCity(userPutProfileDto.getCity());
            user.setPassword(bCryptPasswordEncoder.encode(userPutProfileDto.getNewPassword()));
            user = userRepository.save(user);
            return UserProfileDto.fromUser(user);
        }
        throw new InvalidPasswordException();
    }

    @Transactional
    public void deleteUser(final int userId) {
        userRepository.deleteById(userId);
    }

    private User convertToUser(final UserRegistrationDto userRegistrationDTO) {
        final User user = new User();
        user.setName(userRegistrationDTO.getName());
        user.setLogin(userRegistrationDTO.getLogin());
        user.setCity(userRegistrationDTO.getCity());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setUserRoles(Set.of(roleRepository.getRoleByName("ROLE_USER")));
        user.setAccountNonLocked(true);
        user.setEnabled(false);
        user.setPassword(bCryptPasswordEncoder.encode(userRegistrationDTO.getPassword()));
        return user;
    }
}
