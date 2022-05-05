package ru.bookcrossing.BookcrossingServer.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.chat.model.Correspondence;
import ru.bookcrossing.BookcrossingServer.chat.model.UsersCorrKey;
import ru.bookcrossing.BookcrossingServer.chat.repository.CorrespondenceRepository;
import ru.bookcrossing.BookcrossingServer.chat.repository.MessageRepository;
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;
import ru.bookcrossing.BookcrossingServer.user.model.User;
import ru.bookcrossing.BookcrossingServer.user.repository.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CorrespondenceService {

    private final CorrespondenceRepository correspondenceRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public Optional<ErrorListResponse> createChat(int userId, String login){
        ErrorListResponse response = new ErrorListResponse();
        User fUser = userRepository.findByLogin(login).orElseThrow();
        Optional<User> sUser = userRepository.findById(userId);
        if(sUser.isPresent()){
            if(sUser.get().isEnabled() && sUser.get().isAccountNonLocked()){
                UsersCorrKey usersCorrKey = new UsersCorrKey();
                usersCorrKey.setFirstUser(fUser);
                usersCorrKey.setSecondUser(sUser.get());
                if(correspondenceRepository.findById(usersCorrKey).isPresent()){
                    response.getErrors().add("correspondence: Чат уже существует");
                }
                else{
                    Correspondence correspondence = new Correspondence();
                    correspondence.setUsersCorrKey(usersCorrKey);
                    correspondenceRepository.save(correspondence);
                    return Optional.empty();
                }
            }
            else{
                response.getErrors().add("user: Пользователь заблокирован");
            }
        }
        else{
            response.getErrors().add("user: Пользователь не найден");
        }
        return Optional.of(response);
    }
}
