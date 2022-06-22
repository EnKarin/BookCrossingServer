package io.github.enkarin.bookcrossing.user.service;

import io.github.enkarin.bookcrossing.errors.ErrorListResponse;
import io.github.enkarin.bookcrossing.mail.model.ActionMailUser;
import io.github.enkarin.bookcrossing.mail.repository.ActionMailUserRepository;
import io.github.enkarin.bookcrossing.user.dto.UserPasswordDto;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private final ActionMailUserRepository actionMailUserRepository;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Optional<ErrorListResponse> updatePassword(final String token, final UserPasswordDto passwordDto) {
        final ErrorListResponse response = new ErrorListResponse();
        User user;
        if (!passwordDto.getPassword().equals(passwordDto.getPasswordConfirm())) {
            response.getErrors().add("password: Пароли не совпадают");
            return Optional.of(response);
        }
        final Optional<ActionMailUser> actionMailUser = actionMailUserRepository.findById(token);
        if (actionMailUser.isPresent()) {
            user = actionMailUser.get().getUser();
            user.setPassword(bCryptPasswordEncoder.encode(passwordDto.getPassword()));
            userRepository.save(user);
            actionMailUserRepository.delete(actionMailUser.get());
            return Optional.empty();
        } else {
            response.getErrors().add("token: Токен некорректен");
            return Optional.of(response);
        }
    }
}
