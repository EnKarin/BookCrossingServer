package ru.bookcrossing.BookcrossingServer.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.chat.dto.MessageResponse;
import ru.bookcrossing.BookcrossingServer.chat.dto.ZonedUserCorrKeyDto;
import ru.bookcrossing.BookcrossingServer.chat.model.Correspondence;
import ru.bookcrossing.BookcrossingServer.chat.model.Message;
import ru.bookcrossing.BookcrossingServer.chat.model.UsersCorrKey;
import ru.bookcrossing.BookcrossingServer.chat.repository.CorrespondenceRepository;
import ru.bookcrossing.BookcrossingServer.chat.repository.MessageRepository;
import ru.bookcrossing.BookcrossingServer.exception.ChatAlreadyCreatedException;
import ru.bookcrossing.BookcrossingServer.exception.UserNotFoundException;
import ru.bookcrossing.BookcrossingServer.user.model.User;
import ru.bookcrossing.BookcrossingServer.user.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CorrespondenceService {

    private final CorrespondenceRepository correspondenceRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public Optional<Correspondence> createChat(int userId, String login) {
        User fUser = userRepository.findByLogin(login).orElseThrow();
        Optional<User> sUser = userRepository.findById(userId);
        if (sUser.isPresent()) {
            if (sUser.get().isEnabled() && sUser.get().isAccountNonLocked()) {
                UsersCorrKey usersCorrKey = new UsersCorrKey();
                usersCorrKey.setFirstUser(fUser);
                usersCorrKey.setSecondUser(sUser.get());
                if (correspondenceRepository.findById(usersCorrKey).isPresent()) {
                    throw new ChatAlreadyCreatedException();
                } else {
                    Correspondence correspondence = new Correspondence();
                    correspondence.setUsersCorrKey(usersCorrKey);
                    return Optional.of(correspondenceRepository.save(correspondence));
                }
            } else {
                return Optional.empty();
            }
        } else {
            throw new UserNotFoundException();
        }
    }

    public boolean deleteChat(int userId, String login) {
        UsersCorrKey usersCorrKey = new UsersCorrKey();
        usersCorrKey.setFirstUser(userRepository.findByLogin(login).orElseThrow());
        Optional<User> sUser = userRepository.findById(userId);
        if (sUser.isPresent()) {
            usersCorrKey.setSecondUser(sUser.get());
            Optional<Correspondence> correspondence = correspondenceRepository.findById(usersCorrKey);
            if (correspondence.isPresent()) {
                correspondenceRepository.delete(correspondence.get());
                return true;
            }
        }
        return false;
    }

    public Optional<List<MessageResponse>> getChat(ZonedUserCorrKeyDto zonedUserCorrKeyDto,
                                                   String login) {
        User user = userRepository.findByLogin(login).orElseThrow();
        Optional<User> fUser = userRepository.findById(zonedUserCorrKeyDto.getFirstUserId());
        Optional<User> sUser = userRepository.findById(zonedUserCorrKeyDto.getSecondUserId());
        if (fUser.isPresent() && sUser.isPresent()) {
            UsersCorrKey usersCorrKey = new UsersCorrKey();
            usersCorrKey.setFirstUser(fUser.get());
            usersCorrKey.setSecondUser(sUser.get());
            Optional<Correspondence> correspondence = correspondenceRepository.findById(usersCorrKey);
            if (correspondence.isPresent()) {
                if (user.equals(fUser.get())) {
                    return Optional.of(getMessages(Message::isShownFirstUser, correspondence.get(), zonedUserCorrKeyDto));
                }
                if(user.equals(sUser.get())) {
                    return Optional.of(getMessages(Message::isShownSecondUser, correspondence.get(), zonedUserCorrKeyDto));
                }
            }
        }
        return Optional.empty();
    }

    private List<MessageResponse> getMessages(Predicate<Message> rules, Correspondence correspondence,
                                              ZonedUserCorrKeyDto zonedUserCorrKeyDto) {
        List<MessageResponse> responses = correspondence.getMessage().stream()
                .filter(rules)
                .map(m -> new MessageResponse(m, zonedUserCorrKeyDto.getZone()))
                .sorted(Comparator.comparing(MessageResponse::getDate))
                .collect(Collectors.toList());
        correspondence.setMessage(correspondence.getMessage().stream()
                .filter(Predicate.not(Message::isDeclaim))
                .peek(m -> m.setDeclaim(true))
                .map(messageRepository::save)
                .collect(Collectors.toList()));
        return responses;
    }
}
