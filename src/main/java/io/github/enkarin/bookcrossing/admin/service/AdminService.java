package io.github.enkarin.bookcrossing.admin.service;

import io.github.enkarin.bookcrossing.admin.dto.InfoUsersDto;
import io.github.enkarin.bookcrossing.admin.dto.LockedUserDto;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.mail.service.MailService;
import io.github.enkarin.bookcrossing.user.model.Role;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.RoleRepository;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MailService mailService;

    public void lockedUser(final LockedUserDto lockedUserDto) {
        final User user = userRepository.findByLogin(lockedUserDto.getLogin())
                .orElseThrow(UserNotFoundException::new);
        user.setAccountNonLocked(false);
        userRepository.save(user);
        mailService.sendBlockingMessage(user, lockedUserDto.getComment());

    }

    public void nonLockedUser(final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        user.setAccountNonLocked(true);
        userRepository.save(user);
        mailService.sendUnlockMessage(user);
    }

    public List<InfoUsersDto> findAllUsers(final int zone) {
        final Role role = roleRepository.getById(1);
        return userRepository.findByUserRoles(role).stream()
                .map(u -> InfoUsersDto.fromUser(u, zone))
                .collect(Collectors.toList());
    }
}
