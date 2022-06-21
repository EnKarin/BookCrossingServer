package ru.bookcrossing.bookcrossingserver.refresh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookcrossing.bookcrossingserver.refresh.model.Refresh;
import ru.bookcrossing.bookcrossingserver.refresh.repository.RefreshRepository;
import ru.bookcrossing.bookcrossingserver.user.model.User;
import ru.bookcrossing.bookcrossingserver.user.repository.UserRepository;

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

    public String createToken(final String login) {
        refreshRepository.findByUser(login).ifPresent(refreshRepository::delete);

        final String token = UUID.randomUUID().toString();

        final Refresh refresh = new Refresh();
        refresh.setRefresh(token);
        refresh.setDate(LocalDate.now().plusDays(15).toEpochDay());
        refresh.setUser(login);

        refreshRepository.save(refresh);
        final Optional<User> user = userRepository.findByLogin(login);
        user.orElseThrow().setLoginDate(LocalDateTime.now()
                .toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())));
        userRepository.save(user.get());
        return token;
    }

    public Optional<String> findByToken(final String token) {
        final Optional<Refresh> refresh = refreshRepository.findById(token);
        if(refresh.isPresent()){
            if(LocalDate.ofEpochDay(refresh.get().getDate()).isAfter(LocalDate.now())){
                return Optional.of(refresh.get().getUser());
            }
            else {
                refreshRepository.delete(refresh.get());
                return Optional.empty();
            }
        }
        else {
            return Optional.empty();
        }
    }
}
