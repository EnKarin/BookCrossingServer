package io.github.enkarin.bookcrossing.chat.service;

import io.github.enkarin.bookcrossing.chat.dto.MessageDto;
import io.github.enkarin.bookcrossing.chat.dto.MessagePutRequest;
import io.github.enkarin.bookcrossing.chat.dto.MessageRequest;
import io.github.enkarin.bookcrossing.chat.model.Correspondence;
import io.github.enkarin.bookcrossing.chat.model.Message;
import io.github.enkarin.bookcrossing.chat.model.UsersCorrKey;
import io.github.enkarin.bookcrossing.chat.repository.CorrespondenceRepository;
import io.github.enkarin.bookcrossing.chat.repository.MessageRepository;
import io.github.enkarin.bookcrossing.exception.ChatNotFoundException;
import io.github.enkarin.bookcrossing.exception.MessageNotFountException;
import io.github.enkarin.bookcrossing.exception.NoAccessToChatException;
import io.github.enkarin.bookcrossing.exception.UserIsNotSenderException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import io.github.enkarin.bookcrossing.configuration.TimeSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class MessageService {
    private final TimeSettings timeSettings;
    private final MessageRepository messageRepository;
    private final CorrespondenceRepository correspondenceRepository;
    private final UserRepository userRepository;

    public MessageDto sendMessage(final MessageRequest dto, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        final User firstUser = userRepository.findById(dto.getUsersCorrKeyDto().getFirstUserId())
                .orElseThrow(UserNotFoundException::new);
        final User secondUser = userRepository.findById(dto.getUsersCorrKeyDto().getSecondUserId())
                .orElseThrow(UserNotFoundException::new);
        final UsersCorrKey usersCorrKey = new UsersCorrKey();
        usersCorrKey.setFirstUser(firstUser);
        usersCorrKey.setSecondUser(secondUser);
        if (usersCorrKey.getFirstUser().equals(user) || usersCorrKey.getSecondUser().equals(user)) {
            final Correspondence correspondence = correspondenceRepository.findById(usersCorrKey)
                    .orElseThrow(ChatNotFoundException::new);
            final Message message = new Message();
            message.setText(dto.getText());
            message.setDepartureDate(timeSettings.getEpochSeconds());
            message.setCorrespondence(correspondence);
            message.setSender(user);
            message.setShownFirstUser(true);
            message.setShownSecondUser(true);
            return MessageDto.fromMessage(messageRepository.save(message));
        }
        throw new NoAccessToChatException();
    }

    public MessageDto putMessage(final MessagePutRequest messagePutRequest, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        final Message message = messageRepository.findById(messagePutRequest.getMessageId())
                .orElseThrow(MessageNotFountException::new);
        if (user.equals(message.getSender())) {
            message.setDepartureDate(timeSettings.getEpochSeconds());
            message.setText(messagePutRequest.getText());
            return MessageDto.fromMessage(messageRepository.save(message));
        } else {
            throw new UserIsNotSenderException();
        }
    }

    public void deleteForEveryoneMessage(final long messageId, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        final Message message = messageRepository.findById(messageId)
                .orElseThrow(MessageNotFountException::new);
        if (user.equals(message.getSender())) {
            messageRepository.delete(message);
        } else {
            throw new UserIsNotSenderException();
        }
    }

    public void deleteForMeMessage(final long messageId, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        final Message message = messageRepository.findById(messageId)
                .orElseThrow(MessageNotFountException::new);
        final Correspondence correspondence = message.getCorrespondence();
        if (correspondence.getUsersCorrKey().getFirstUser().equals(user)) {
            message.setShownFirstUser(false);
        } else if (correspondence.getUsersCorrKey().getSecondUser().equals(user)) {
            message.setShownSecondUser(false);
        } else {
            throw new NoAccessToChatException();
        }
        messageRepository.save(message);
    }
}
