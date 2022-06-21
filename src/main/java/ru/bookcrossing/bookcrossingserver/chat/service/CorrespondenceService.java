package ru.bookcrossing.bookcrossingserver.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookcrossing.bookcrossingserver.chat.dto.MessageResponse;
import ru.bookcrossing.bookcrossingserver.chat.dto.UsersCorrKeyDto;
import ru.bookcrossing.bookcrossingserver.chat.dto.ZonedUserCorrKeyDto;
import ru.bookcrossing.bookcrossingserver.chat.model.Correspondence;
import ru.bookcrossing.bookcrossingserver.chat.model.Message;
import ru.bookcrossing.bookcrossingserver.chat.model.UsersCorrKey;
import ru.bookcrossing.bookcrossingserver.chat.repository.CorrespondenceRepository;
import ru.bookcrossing.bookcrossingserver.chat.repository.MessageRepository;
import ru.bookcrossing.bookcrossingserver.exception.ChatAlreadyCreatedException;
import ru.bookcrossing.bookcrossingserver.exception.UserNotFoundException;
import ru.bookcrossing.bookcrossingserver.user.model.User;
import ru.bookcrossing.bookcrossingserver.user.repository.UserRepository;

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

    public Optional<UsersCorrKeyDto> createChat(final int userId, final String login) {
        final User fUser = userRepository.findByLogin(login).orElseThrow();
        final Optional<User> sUser = userRepository.findById(userId);
        if (sUser.isPresent()) {
            if (sUser.get().isEnabled() && sUser.get().isAccountNonLocked()) {
                final UsersCorrKey usersCorrKey = new UsersCorrKey();
                usersCorrKey.setFirstUser(fUser);
                usersCorrKey.setSecondUser(sUser.get());
                if (correspondenceRepository.findById(usersCorrKey).isPresent()) {
                    throw new ChatAlreadyCreatedException();
                } else {
                    Correspondence correspondence = new Correspondence();
                    correspondence.setUsersCorrKey(usersCorrKey);
                    correspondence = correspondenceRepository.save(correspondence);
                    final UsersCorrKeyDto result = new UsersCorrKeyDto();
                    result.setFirstUserId(correspondence.getUsersCorrKey().getFirstUser().getUserId());
                    result.setSecondUserId(correspondence.getUsersCorrKey().getSecondUser().getUserId());
                    return Optional.of(result);
                }
            } else {
                return Optional.empty();
            }
        } else {
            throw new UserNotFoundException();
        }
    }

    public boolean deleteChat(final int userId, final String login) {
        final UsersCorrKey usersCorrKey = new UsersCorrKey();
        usersCorrKey.setFirstUser(userRepository.findByLogin(login).orElseThrow());
        final Optional<User> sUser = userRepository.findById(userId);
        if (sUser.isPresent()) {
            usersCorrKey.setSecondUser(sUser.get());
            final Optional<Correspondence> correspondence = correspondenceRepository.findById(usersCorrKey);
            if (correspondence.isPresent()) {
                correspondenceRepository.delete(correspondence.get());
                return true;
            }
        }
        return false;
    }

    public Optional<List<MessageResponse>> getChat(final ZonedUserCorrKeyDto zonedUserCorrKeyDto,
                                                   final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        final Optional<User> fUser = userRepository.findById(zonedUserCorrKeyDto.getFirstUserId());
        final Optional<User> sUser = userRepository.findById(zonedUserCorrKeyDto.getSecondUserId());
        if (fUser.isPresent() && sUser.isPresent()) {
            final UsersCorrKey usersCorrKey = new UsersCorrKey();
            usersCorrKey.setFirstUser(fUser.get());
            usersCorrKey.setSecondUser(sUser.get());
            final Optional<Correspondence> correspondence = correspondenceRepository.findById(usersCorrKey);
            if (correspondence.isPresent()) {
                if (user.equals(fUser.get())) {
                    return Optional.of(getMessages(Message::isShownFirstUser, correspondence.get(), zonedUserCorrKeyDto,
                            user));
                }
                if(user.equals(sUser.get())) {
                    return Optional.of(getMessages(Message::isShownSecondUser, correspondence.get(), zonedUserCorrKeyDto,
                            user));
                }
            }
        }
        return Optional.empty();
    }

    private List<MessageResponse> getMessages(final Predicate<Message> rules, final Correspondence correspondence,
                                              final ZonedUserCorrKeyDto zonedUserCorrKeyDto, final User user) {
        final List<MessageResponse> responses = correspondence.getMessage().stream()
                .filter(rules)
                .map(m -> new MessageResponse(m, zonedUserCorrKeyDto.getZone()))
                .sorted(Comparator.comparing(MessageResponse::getDate))
                .collect(Collectors.toList());
        correspondence.setMessage(correspondence.getMessage().stream()
                .filter(m -> !user.equals(m.getSender()))
                .filter(Predicate.not(Message::isDeclaim))
                .peek(m -> m.setDeclaim(true))
                .map(messageRepository::save)
                .collect(Collectors.toList()));
        return responses;
    }
}
