package ru.bookcrossing.bookcrossingserver.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookcrossing.bookcrossingserver.admin.dto.InfoUsersDto;
import ru.bookcrossing.bookcrossingserver.admin.dto.LockedUserDto;
import ru.bookcrossing.bookcrossingserver.mail.service.MailService;
import ru.bookcrossing.bookcrossingserver.user.model.Role;
import ru.bookcrossing.bookcrossingserver.user.model.User;
import ru.bookcrossing.bookcrossingserver.user.repository.RoleRepository;
import ru.bookcrossing.bookcrossingserver.user.repository.UserRepository;

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
        } else{
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
