package io.github.enkarin.bookcrossing.user.service;

import io.github.enkarin.bookcrossing.exception.AccountNotConfirmedException;
import io.github.enkarin.bookcrossing.exception.EmailFailedException;
import io.github.enkarin.bookcrossing.exception.InvalidPasswordException;
import io.github.enkarin.bookcrossing.exception.LockedAccountException;
import io.github.enkarin.bookcrossing.exception.LoginFailedException;
import io.github.enkarin.bookcrossing.exception.PasswordsDontMatchException;
import io.github.enkarin.bookcrossing.exception.TokenNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
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
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.RoleRepository;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import io.github.enkarin.bookcrossing.utils.ImageCompressor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;

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
        if (isNull(userRegistrationDTO.getLogin()) || userRegistrationDTO.getLogin().isBlank()) {
            userRegistrationDTO.setLogin(generateLogin());
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

    public List<UserPublicProfileDto> findAllUsers(final int zone, final int pageNumber, final int pageSize) {
        return userRepository.findByUserRolesOrderByUserId("ROLE_USER", pageNumber, pageSize).stream()
            .map(u -> UserPublicProfileDto.fromUser(u, zone))
            .toList();
    }

    public AuthResponse findByLoginAndPassword(final LoginRequest login) {
        final User user = userRepository.findByLogin(login.getLogin()).orElseGet(() -> userRepository.findByEmail(login.getLogin()).orElseThrow(UserNotFoundException::new));
        if (bCryptPasswordEncoder.matches(login.getPassword(), user.getPassword())) {
            if (user.isEnabled()) {
                if (user.isAccountNonLocked()) {
                    return refreshService.createTokens(user.getLogin());
                }
                throw new LockedAccountException();
            }
            throw new AccountNotConfirmedException();
        }
        throw new InvalidPasswordException();
    }

    @Transactional
    public UserProfileDto putUserInfo(final UserPutProfileDto userPutProfileDto, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        if (Objects.nonNull(userPutProfileDto.getName())) {
            user.setName(userPutProfileDto.getName());
        }
        if (Objects.nonNull(userPutProfileDto.getCity())) {
           user.setCity(userPutProfileDto.getCity());
        }
        if (Objects.nonNull(userPutProfileDto.getNewPassword())) {
           checkAndUpdatePassword(user, userPutProfileDto);
        }

        return UserProfileDto.fromUser(user);
    }

    @Transactional
    public void deleteUser(final int userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public void putAvatar(final String login, final MultipartFile avatarData) throws IOException {
        userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new)
            .setAvatar(ImageCompressor.compressImage(ImageIO.read(avatarData.getInputStream()), 150, 150));
    }

    public byte[] getAvatar(final int userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new).getAvatar();
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
        user.setAboutMe(userRegistrationDTO.getAboutMe());
        return user;
    }

    private String generateLogin() {
        String possibleLogin;
        do {
            possibleLogin = UUID.randomUUID().toString();
        } while (userRepository.findByLogin(possibleLogin).isPresent());
        return possibleLogin;
    }

    private void checkAndUpdatePassword(final User user, final UserPutProfileDto userPutProfileDto) {
        if (!userPutProfileDto.getNewPassword().equals(userPutProfileDto.getPasswordConfirm())) {
            throw new PasswordsDontMatchException();
        }
        if (bCryptPasswordEncoder.matches(userPutProfileDto.getOldPassword(), user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(userPutProfileDto.getNewPassword()));
        } else {
            throw new InvalidPasswordException();
        }
    }
}
