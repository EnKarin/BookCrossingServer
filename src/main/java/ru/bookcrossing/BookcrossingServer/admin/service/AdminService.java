package ru.bookcrossing.BookcrossingServer.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.admin.dto.LockedUserDto;
import ru.bookcrossing.BookcrossingServer.mail.service.MailService;
import ru.bookcrossing.BookcrossingServer.user.model.User;
import ru.bookcrossing.BookcrossingServer.user.repository.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final MailService mailService;

    public boolean lockedUser(LockedUserDto lockedUserDto) {
        Optional<User> user = userRepository.findByLogin(lockedUserDto.getLogin());
        if (user.isPresent()) {
            user.get().setAccountNonLocked(false);
            userRepository.save(user.get());
            mailService.sendBlockingMessage(user.get(), lockedUserDto.getComment());
            return true;
        } else return false;
    }

    public boolean nonLockedUser(String login) {
        Optional<User> user = userRepository.findByLogin(login);
        if (user.isPresent()) {
            user.get().setAccountNonLocked(true);
            userRepository.save(user.get());
            mailService.sendUnlockMessage(user.get());
            return true;
        } else return false;
    }
}
