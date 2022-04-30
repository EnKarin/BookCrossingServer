package ru.bookcrossing.BookcrossingServer.refresh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.refresh.model.Refresh;
import ru.bookcrossing.BookcrossingServer.refresh.repository.RefreshRepository;
import ru.bookcrossing.BookcrossingServer.user.model.User;
import ru.bookcrossing.BookcrossingServer.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshService {

    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;

    public String createToken(String login) {
        refreshRepository.findByUser(login).ifPresent(refreshRepository::delete);

        String token = UUID.randomUUID().toString();

        Refresh refresh = new Refresh();
        refresh.setRefresh(token);
        refresh.setDate(LocalDate.now().plusDays(15).toEpochDay());
        refresh.setUser(login);

        refreshRepository.save(refresh);
        Optional<User> user = userRepository.findByLogin(login);
        user.get().setLoginDate(LocalDateTime.now()
                .toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())));
        userRepository.save(user.get());
        return token;
    }

    public Optional<String> findByToken(String token) {
        Optional<Refresh> refresh = refreshRepository.findById(token);
        if(refresh.isPresent()){
            if(LocalDate.ofEpochDay(refresh.get().getDate()).isAfter(LocalDate.now())){
                return Optional.of(refresh.get().getUser());
            }
            else {
                refreshRepository.delete(refresh.get());
                return Optional.empty();
            }
        }
        else return Optional.empty();
    }
}
