package ru.bookcrossing.BookcrossingServer.chat.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.chat.dto.MessageRequest;
import ru.bookcrossing.BookcrossingServer.chat.model.Correspondence;
import ru.bookcrossing.BookcrossingServer.chat.model.Message;
import ru.bookcrossing.BookcrossingServer.chat.model.UsersCorrKey;
import ru.bookcrossing.BookcrossingServer.chat.repository.CorrespondenceRepository;
import ru.bookcrossing.BookcrossingServer.chat.repository.MessageRepository;
import ru.bookcrossing.BookcrossingServer.errors.ErrorListResponse;
import ru.bookcrossing.BookcrossingServer.user.model.User;
import ru.bookcrossing.BookcrossingServer.user.repository.UserRepository;

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
    private final ModelMapper modelMapper;

    public Optional<Message> sendMessage(MessageRequest dto, String login){
        User user = userRepository.findByLogin(login).orElseThrow();
        Optional<User> firstUser = userRepository.findById(dto.getUsersCorrKeyDto().getFirstUserId());
        Optional<User> secondUser = userRepository.findById(dto.getUsersCorrKeyDto().getSecondUserId());
        if(firstUser.isPresent() && secondUser.isPresent()) {
            UsersCorrKey usersCorrKey = new UsersCorrKey();
            usersCorrKey.setFirstUser(firstUser.get());
            usersCorrKey.setSecondUser(secondUser.get());
            if (usersCorrKey.getFirstUser().equals(user) || usersCorrKey.getSecondUser().equals(user)) {
                Optional<Correspondence> correspondence = correspondenceRepository.findById(usersCorrKey);
                if (correspondence.isPresent()) {
                    Message message = modelMapper.map(dto, Message.class);
                    message.setDate(LocalDateTime.now()
                            .toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())));
                    message.setCorrespondence(correspondence.get());
                    message.setSender(user);
                    message = messageRepository.save(message);
                    return Optional.of(message);
                }
            }
        }
        return Optional.empty();
    }

    public ErrorListResponse deleteMessage(long messageId, String login){
        ErrorListResponse response = new ErrorListResponse();
        User user = userRepository.findByLogin(login).orElseThrow();
        Optional<Message> message = messageRepository.findById(messageId);
        if(message.isPresent()){
            Correspondence correspondence = message.get().getCorrespondence();
            if(correspondence.getUsersCorrKey().getFirstUser().equals(user)
                    || correspondence.getUsersCorrKey().getSecondUser().equals(user)){
                if(user.equals(message.get().getSender())) {
                    messageRepository.delete(message.get());
                }
                else {
                    response.getErrors().add("message: Пользователь не является отправителем");
                }
            }
            else{
                response.getErrors().add("correspondence: Нет доступа к чату");
            }
        }
        else{
            response.getErrors().add("message: Сообщения не существует");
        }
        return response;
    }
}
