package io.github.enkarin.bookcrossing.chat.service;

import io.github.enkarin.bookcrossing.chat.dto.MessageDto;
import io.github.enkarin.bookcrossing.chat.dto.UsersCorrKeyDto;
import io.github.enkarin.bookcrossing.chat.model.Correspondence;
import io.github.enkarin.bookcrossing.chat.model.Message;
import io.github.enkarin.bookcrossing.chat.model.UsersCorrKey;
import io.github.enkarin.bookcrossing.chat.repository.CorrespondenceRepository;
import io.github.enkarin.bookcrossing.exception.CannotBeCreatedCorrespondenceException;
import io.github.enkarin.bookcrossing.exception.ChatAlreadyCreatedException;
import io.github.enkarin.bookcrossing.exception.ChatNotFoundException;
import io.github.enkarin.bookcrossing.exception.NoAccessToChatException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CorrespondenceService {

    private final CorrespondenceRepository correspondenceRepository;
    private final CorrespondenceServiceHelper correspondenceServiceHelper;
    private final UserRepository userRepository;

    @Transactional
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

    @Transactional
    public void deleteChat(final int userId, final String login) {
        final UsersCorrKey usersCorrKey = new UsersCorrKey();
        usersCorrKey.setFirstUser(userRepository.findByLogin(login).orElseThrow());
        final User sUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        usersCorrKey.setSecondUser(sUser);
        final Correspondence correspondence = correspondenceRepository.findById(usersCorrKey)
            .orElseThrow(ChatNotFoundException::new);
        correspondenceRepository.delete(correspondence);
    }

    public List<MessageDto> getChat(final int firstUserId, final int secondUserId, final int zone, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        final User fUser = userRepository.findById(firstUserId)
            .orElseThrow(UserNotFoundException::new);
        final User sUser = userRepository.findById(secondUserId)
            .orElseThrow(UserNotFoundException::new);
        final UsersCorrKey usersCorrKey = new UsersCorrKey();
        usersCorrKey.setFirstUser(fUser);
        usersCorrKey.setSecondUser(sUser);
        final Correspondence correspondence = correspondenceRepository.findById(usersCorrKey)
            .orElseThrow(ChatNotFoundException::new);
        if (user.equals(fUser)) {
            return correspondenceServiceHelper.getMessages(Message::isShownFirstUser, correspondence, zone, user);
        } else if (user.equals(sUser)) {
            return correspondenceServiceHelper.getMessages(Message::isShownSecondUser, correspondence, zone, user);
        }
        throw new NoAccessToChatException();
    }
}
