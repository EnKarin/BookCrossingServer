package io.github.enkarin.bookcrossing.user.service;

import io.github.enkarin.bookcrossing.exception.TokenInvalidException;
import io.github.enkarin.bookcrossing.mail.model.ActionMailUser;
import io.github.enkarin.bookcrossing.mail.repository.ActionMailUserRepository;
import io.github.enkarin.bookcrossing.user.dto.UserPasswordDto;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ResetPasswordService {

    private final ActionMailUserRepository actionMailUserRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void updatePassword(final String token, final UserPasswordDto passwordDto) {
        final ActionMailUser actionMailUser = actionMailUserRepository.findById(token)
                .orElseThrow(TokenInvalidException::new);
        final User user = actionMailUser.getUser();
        user.setPassword(bCryptPasswordEncoder.encode(passwordDto.getPassword()));
        userRepository.save(user);
        actionMailUserRepository.delete(actionMailUser);
    }
}
