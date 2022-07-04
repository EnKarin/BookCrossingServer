package io.github.enkarin.bookcrossing.refresh.service;

import io.github.enkarin.bookcrossing.exception.RefreshTokenInvalidException;
import io.github.enkarin.bookcrossing.exception.TokenNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.refresh.model.Refresh;
import io.github.enkarin.bookcrossing.refresh.repository.RefreshRepository;
import io.github.enkarin.bookcrossing.registation.dto.AuthResponse;
import io.github.enkarin.bookcrossing.security.jwt.JwtProvider;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(noRollbackFor = RefreshTokenInvalidException.class)
public class RefreshService {

    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public AuthResponse createTokens(final String token) {
        final Refresh refresh = refreshRepository.findById(token).orElseThrow(TokenNotFoundException::new);
        if (LocalDate.ofEpochDay(refresh.getDate()).isAfter(LocalDate.now())) {
            return AuthResponse.create(jwtProvider.generateToken(refresh.getUser()),
                    createAccess(refresh.getUser()));
        } else {
            refreshRepository.delete(refresh);
            throw new RefreshTokenInvalidException();
        }
    }

    private String createAccess(final String login) {
        refreshRepository.findByUser(login).ifPresent(refreshRepository::delete);

        final String token = UUID.randomUUID().toString();

        final Refresh refresh = new Refresh();
        refresh.setRefresh(token);
        refresh.setDate(LocalDate.now().plusDays(15).toEpochDay());
        refresh.setUser(login);
        refreshRepository.save(refresh);

        final User user = userRepository.findByLogin(login)
                .orElseThrow(UserNotFoundException::new);
        user.setLoginDate(LocalDateTime.now()
                .toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())));
        userRepository.save(user);
        return token;
    }
}
