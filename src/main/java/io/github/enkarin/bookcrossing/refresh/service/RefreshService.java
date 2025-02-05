package io.github.enkarin.bookcrossing.refresh.service;

import io.github.enkarin.bookcrossing.configuration.TimeSettings;
import io.github.enkarin.bookcrossing.exception.RefreshTokenInvalidException;
import io.github.enkarin.bookcrossing.exception.TokenNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.refresh.model.Refresh;
import io.github.enkarin.bookcrossing.refresh.repository.RefreshRepository;
import io.github.enkarin.bookcrossing.registration.dto.AuthResponse;
import io.github.enkarin.bookcrossing.security.jwt.JwtProvider;
import io.github.enkarin.bookcrossing.user.model.User;
import io.github.enkarin.bookcrossing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshService {
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final TimeSettings timeSettings;

    @Transactional(noRollbackFor = RefreshTokenInvalidException.class)
    public AuthResponse updateTokens(final String token) {
        final Refresh refresh = refreshRepository.findById(token).orElseThrow(TokenNotFoundException::new);
        if (refresh.getDate() >= timeSettings.getEpochSeconds()) {
            return createTokens(refresh.getUser());
        } else {
            refreshRepository.delete(refresh);
            throw new RefreshTokenInvalidException();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuthResponse createTokens(final String login) {
        final User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        user.setLoginDate(timeSettings.getEpochSeconds());

        refreshRepository.findByUser(login).ifPresent(refreshRepository::delete);

        final String token = UUID.randomUUID().toString();

        final Refresh refresh = new Refresh();
        refresh.setRefreshId(token);
        refresh.setDate(timeSettings.dateTimeNow().plusDays(3).toEpochSecond(timeSettings.offset()));
        refresh.setUser(login);
        refreshRepository.save(refresh);

        return AuthResponse.create(jwtProvider.generateToken(login), token);
    }
}
