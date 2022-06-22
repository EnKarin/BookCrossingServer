package io.github.enkarin.bookcrossing.admin.service;

import io.github.enkarin.bookcrossing.admin.dto.InfoUsersDto;
import io.github.enkarin.bookcrossing.admin.dto.LockedUserDto;
import io.github.enkarin.bookcrossing.mail.service.MailService;
import io.github.enkarin.bookcrossing.user.model.Role;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.RoleRepository;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MailService mailService;

    public boolean lockedUser(final LockedUserDto lockedUserDto) {
        final Optional<User> user = userRepository.findByLogin(lockedUserDto.getLogin());
        if (user.isPresent()) {
            user.get().setAccountNonLocked(false);
            userRepository.save(user.get());
            mailService.sendBlockingMessage(user.get(), lockedUserDto.getComment());
            return true;
        } else {
            return false;
        }
    }

    public boolean nonLockedUser(final String login) {
        final Optional<User> user = userRepository.findByLogin(login);
        if (user.isPresent()) {
            user.get().setAccountNonLocked(true);
            userRepository.save(user.get());
            mailService.sendUnlockMessage(user.get());
            return true;
        } else {
            return false;
        }
    }

    public List<InfoUsersDto> findAllUsers(final int zone) {
        final Role role = roleRepository.getById(1);
        return userRepository.findByUserRoles(role).stream()
                .map(u -> new InfoUsersDto(u, zone))
                .collect(Collectors.toList());
    }
}
