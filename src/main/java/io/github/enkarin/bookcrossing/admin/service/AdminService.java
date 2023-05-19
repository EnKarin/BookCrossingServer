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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MailService mailService;

    @Transactional
    public boolean lockedUser(final LockedUserDto lockedUserDto) {
        User user = userRepository.findByLogin(lockedUserDto.getLogin())
                .orElseThrow(UserNotFoundException::new);
        user.setAccountNonLocked(false);
        user = userRepository.save(user);
        mailService.sendBlockingMessage(user, lockedUserDto.getComment());
        return user.isAccountNonLocked();
    }

    @Transactional
    public boolean nonLockedUser(final String login) {
        User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        user.setAccountNonLocked(true);
        user = userRepository.save(user);
        mailService.sendUnlockMessage(user);
        return user.isAccountNonLocked();
    }

    public List<InfoUsersDto> findAllUsers(final int zone) {
        final Role role = roleRepository.getRoleByName("ROLE_USER");
        return userRepository.findByUserRolesOrderByUserId(role).stream()
                .map(u -> InfoUsersDto.fromUser(u, zone))
                .toList();
    }
}
