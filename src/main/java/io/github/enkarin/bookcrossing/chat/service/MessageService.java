package io.github.enkarin.bookcrossing.chat.service;

import io.github.enkarin.bookcrossing.chat.dto.MessagePutRequest;
import io.github.enkarin.bookcrossing.chat.dto.MessageRequest;
import io.github.enkarin.bookcrossing.chat.model.Correspondence;
import io.github.enkarin.bookcrossing.chat.model.Message;
import io.github.enkarin.bookcrossing.chat.model.UsersCorrKey;
import io.github.enkarin.bookcrossing.chat.repository.CorrespondenceRepository;
import io.github.enkarin.bookcrossing.chat.repository.MessageRepository;
import io.github.enkarin.bookcrossing.errors.ErrorListResponse;
import io.github.enkarin.bookcrossing.exception.MessageNotFountException;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final CorrespondenceRepository correspondenceRepository;
    private final UserRepository userRepository;

    public Optional<Message> sendMessage(final MessageRequest dto, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        final Optional<User> firstUser = userRepository.findById(dto.getUsersCorrKeyDto().getFirstUserId());
        final Optional<User> secondUser = userRepository.findById(dto.getUsersCorrKeyDto().getSecondUserId());
        if (firstUser.isPresent() && secondUser.isPresent()) {
            final UsersCorrKey usersCorrKey = new UsersCorrKey();
            usersCorrKey.setFirstUser(firstUser.get());
            usersCorrKey.setSecondUser(secondUser.get());
            if (usersCorrKey.getFirstUser().equals(user) || usersCorrKey.getSecondUser().equals(user)) {
                final Optional<Correspondence> correspondence = correspondenceRepository.findById(usersCorrKey);
                if (correspondence.isPresent()) {
                    final Message message = new Message();
                    message.setText(dto.getText());
                    message.setDepartureDate(LocalDateTime.now()
                            .toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())));
                    message.setCorrespondence(correspondence.get());
                    message.setSender(user);
                    message.setShownFirstUser(true);
                    message.setShownSecondUser(true);
                    return Optional.of(messageRepository.save(message));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Message> putMessage(final MessagePutRequest messagePutRequest, final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow();
        final Optional<Message> message = messageRepository.findById(messagePutRequest.getMessageId());
        if (message.isPresent() && user.equals(message.get().getSender())) {
            final Correspondence correspondence = message.get().getCorrespondence();
            if (user.equals(correspondence.getUsersCorrKey().getFirstUser()) ||
                    user.equals(correspondence.getUsersCorrKey().getSecondUser())) {
                message.get().setDepartureDate(LocalDateTime.now()
                        .toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())));
                message.get().setText(messagePutRequest.getText());
                return Optional.of(messageRepository.save(message.get()));
            } else {
                return Optional.empty();
            }
        } else {
            throw new MessageNotFountException();
        }
    }

    public ErrorListResponse deleteForEveryoneMessage(final long messageId, final String login) {
        final ErrorListResponse response = new ErrorListResponse();
        final User user = userRepository.findByLogin(login).orElseThrow();
        final Optional<Message> message = messageRepository.findById(messageId);
        if (message.isPresent()) {
            final Correspondence correspondence = message.get().getCorrespondence();
            if (correspondence.getUsersCorrKey().getFirstUser().equals(user) ||
                    correspondence.getUsersCorrKey().getSecondUser().equals(user)) {
                if (user.equals(message.get().getSender())) {
                    messageRepository.delete(message.get());
                } else {
                    response.getErrors().add("message: Пользователь не является отправителем");
                }
            } else {
                response.getErrors().add("correspondence: Нет доступа к чату");
            }
        } else {
            response.getErrors().add("message: Сообщения не существует");
        }
        return response;
    }

    public ErrorListResponse deleteForMeMessage(final long messageId, final String login) {
        final ErrorListResponse response = new ErrorListResponse();
        final User user = userRepository.findByLogin(login).orElseThrow();
        final Optional<Message> message = messageRepository.findById(messageId);
        if (message.isPresent()) {
            final Correspondence correspondence = message.get().getCorrespondence();
            if (user.equals(message.get().getSender())) {
                if (correspondence.getUsersCorrKey().getFirstUser().equals(user)) {
                    message.get().setShownFirstUser(false);
                    messageRepository.save(message.get());
                }
                if (correspondence.getUsersCorrKey().getSecondUser().equals(user)) {
                    message.get().setShownSecondUser(false);
                    messageRepository.save(message.get());
                }
            } else {
                response.getErrors().add("message: Пользователь не является отправителем");
            }
        } else {
            response.getErrors().add("message: Сообщения не существует");
        }
        return response;
    }
}
