package ru.bookcrossing.BookcrossingServer.chat.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.chat.dto.MessageDto;
import ru.bookcrossing.BookcrossingServer.chat.model.Correspondence;
import ru.bookcrossing.BookcrossingServer.chat.model.Message;
import ru.bookcrossing.BookcrossingServer.chat.model.UsersCorrKey;
import ru.bookcrossing.BookcrossingServer.chat.repository.CorrespondenceRepository;
import ru.bookcrossing.BookcrossingServer.chat.repository.MessageRepository;
import ru.bookcrossing.BookcrossingServer.user.model.User;
import ru.bookcrossing.BookcrossingServer.user.repository.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final CorrespondenceRepository correspondenceRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public Optional<Message> sendMessage(MessageDto dto, String login){
        User user = userRepository.findByLogin(login).orElseThrow();
        Optional<User> firstUser = userRepository.findById(dto.getUsersCorrKeyDto().getFirstUserId());
        Optional<User> secondUser = userRepository.findById(dto.getUsersCorrKeyDto().getSecondUserId());
        if(firstUser.isPresent() && secondUser.isPresent()) {
            UsersCorrKey usersCorrKey = new UsersCorrKey();
            usersCorrKey.setFirstUser(firstUser.get());
            usersCorrKey.setSecondUser(secondUser.get());
            if (usersCorrKey.getFirstUser() == user || usersCorrKey.getSecondUser() == user) {
                Optional<Correspondence> correspondence = correspondenceRepository.findById(usersCorrKey);
                if (correspondence.isPresent()) {
                    Message message = modelMapper.map(dto, Message.class);
                    message.setCorrespondence(correspondence.get());
                    message.setSender(user);
                    message = messageRepository.save(message);
                    return Optional.of(message);
                }
            }
        }
        return Optional.empty();
    }
}
