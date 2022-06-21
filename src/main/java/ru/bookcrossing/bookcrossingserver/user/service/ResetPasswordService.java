package ru.bookcrossing.bookcrossingserver.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bookcrossing.bookcrossingserver.errors.ErrorListResponse;
import ru.bookcrossing.bookcrossingserver.mail.model.ActionMailUser;
import ru.bookcrossing.bookcrossingserver.mail.repository.ActionMailUserRepository;
import ru.bookcrossing.bookcrossingserver.user.dto.UserPasswordDto;
import ru.bookcrossing.bookcrossingserver.user.model.User;
import ru.bookcrossing.bookcrossingserver.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private final ActionMailUserRepository actionMailUserRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Optional<ErrorListResponse> updatePassword(final String token, final UserPasswordDto passwordDto){
        final ErrorListResponse response = new ErrorListResponse();
        User user;
        if(!passwordDto.getPassword().equals(passwordDto.getPasswordConfirm())){
            response.getErrors().add("password: Пароли не совпадают");
            return Optional.of(response);
        }
        final Optional<ActionMailUser> actionMailUser = actionMailUserRepository.findById(token);
        if(actionMailUser.isPresent()){
            user = actionMailUser.get().getUser();
            user.setPassword(bCryptPasswordEncoder.encode(passwordDto.getPassword()));
            userRepository.save(user);
            actionMailUserRepository.delete(actionMailUser.get());
            return Optional.empty();
        }
        else{
            response.getErrors().add("token: Токен некорректен");
            return Optional.of(response);
        }
    }
}
