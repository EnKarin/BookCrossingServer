package ru.bookcrossing.BookcrossingServer.refresh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.refresh.model.Refresh;
import ru.bookcrossing.BookcrossingServer.refresh.repository.RefreshRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshService {

    private final RefreshRepository refreshRepository;

    public String createToken(String login) {
        refreshRepository.findByUser(login).ifPresent(refreshRepository::delete);

        String token = UUID.randomUUID().toString();

        Refresh refresh = new Refresh();
        refresh.setRefresh(token);
        refresh.setDate(LocalDate.now().plusDays(15).toEpochDay());
        refresh.setUser(login);

        refreshRepository.save(refresh);

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
