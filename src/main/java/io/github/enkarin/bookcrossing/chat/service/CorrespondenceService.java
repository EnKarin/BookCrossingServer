package io.github.enkarin.bookcrossing.chat.service;

import io.github.enkarin.bookcrossing.chat.dto.MessageDto;
import io.github.enkarin.bookcrossing.chat.dto.UsersCorrKeyDto;
import io.github.enkarin.bookcrossing.chat.dto.ZonedUserCorrKeyDto;
import io.github.enkarin.bookcrossing.chat.model.Correspondence;
import io.github.enkarin.bookcrossing.chat.model.Message;
import io.github.enkarin.bookcrossing.chat.model.UsersCorrKey;
import io.github.enkarin.bookcrossing.chat.repository.CorrespondenceRepository;
import io.github.enkarin.bookcrossing.chat.repository.MessageRepository;
import io.github.enkarin.bookcrossing.exception.CannotBeCreatedCorrespondenceException;
import io.github.enkarin.bookcrossing.exception.ChatAlreadyCreatedException;
import io.github.enkarin.bookcrossing.exception.ChatNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CorrespondenceService {

    private final CorrespondenceRepository correspondenceRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public UsersCorrKeyDto createChat(final int userId, final String login) {
        final User fUser = userRepository.findByLogin(login).orElseThrow();
        final User sUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (sUser.isEnabled() && sUser.isAccountNonLocked()) {
            final UsersCorrKey usersCorrKey = new UsersCorrKey();
            usersCorrKey.setFirstUser(fUser);
            usersCorrKey.setSecondUser(sUser);
            if (correspondenceRepository.findById(usersCorrKey).isPresent()) {
                throw new ChatAlreadyCreatedException();
            } else {
                Correspondence correspondence = new Correspondence();
                correspondence.setUsersCorrKey(usersCorrKey);
                correspondence = correspondenceRepository.save(correspondence);
                return UsersCorrKeyDto.fromCorrespondence(correspondence);
            }
        } else {
            throw new CannotBeCreatedCorrespondenceException();
        }
    }

    public void deleteChat(final int userId, final String login) {
        final UsersCorrKey usersCorrKey = new UsersCorrKey();
        usersCorrKey.setFirstUser(userRepository.findByLogin(login).orElseThrow());
        final User sUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        usersCorrKey.setSecondUser(sUser);
        final Correspondence correspondence = correspondenceRepository.findById(usersCorrKey)
                .orElseThrow(ChatNotFoundException::new);
        correspondenceRepository.delete(correspondence);
    }

    public List<MessageDto> getChat(final ZonedUserCorrKeyDto zonedUserCorrKeyDto,
                                    final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        final User fUser = userRepository.findById(zonedUserCorrKeyDto.getFirstUserId())
                .orElseThrow(UserNotFoundException::new);
        final User sUser = userRepository.findById(zonedUserCorrKeyDto.getSecondUserId())
                .orElseThrow(UserNotFoundException::new);
        final UsersCorrKey usersCorrKey = new UsersCorrKey();
        usersCorrKey.setFirstUser(fUser);
        usersCorrKey.setSecondUser(sUser);
        final Correspondence correspondence = correspondenceRepository.findById(usersCorrKey)
                .orElseThrow(ChatNotFoundException::new);
        if (user.equals(fUser)) {
            return getMessages(Message::isShownFirstUser, correspondence, zonedUserCorrKeyDto, user);
        }
        if (user.equals(sUser)) {
            return getMessages(Message::isShownSecondUser, correspondence, zonedUserCorrKeyDto, user);
        }
        return Collections.emptyList();
    }

    private List<MessageDto> getMessages(final Predicate<Message> rules, final Correspondence correspondence,
                                         final ZonedUserCorrKeyDto zonedUserCorrKeyDto, final User user) {
        final List<MessageDto> responses = correspondence.getMessage().stream()
                .filter(rules)
                .map(m -> MessageDto.fromMessageAndZone(m, zonedUserCorrKeyDto.getZone()))
                .sorted(Comparator.comparing(MessageDto::getDepartureDate))
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
