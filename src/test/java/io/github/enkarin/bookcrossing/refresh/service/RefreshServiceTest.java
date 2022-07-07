package io.github.enkarin.bookcrossing.refresh.service;

import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import io.github.enkarin.bookcrossing.exception.RefreshTokenInvalidException;
import io.github.enkarin.bookcrossing.exception.TokenNotFoundException;
import io.github.enkarin.bookcrossing.exception.UserNotFoundException;
import io.github.enkarin.bookcrossing.registation.dto.AuthResponse;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshServiceTest extends BookCrossingBaseTests {

    @Autowired
    private RefreshService refreshService;

    @AfterEach
    void delete() {
        usersId.forEach(u -> userService.deleteUser(u));
        usersId.clear();
    }

    @Test
    void createTokensTest() {
        final User user = userService.saveUser(TestDataProvider.buildAlex());
        usersId.add(user.getUserId());
        final AuthResponse tokens = refreshService.createTokens(user.getLogin());
        assertThat(jdbcTemplate.queryForObject("select exists(select * from t_refresh where refresh = ?)",
                Boolean.class, tokens.getRefreshToken())).isTrue();
    }

    @Test
    void createTokensExceptionTest() {
        assertThatThrownBy(() -> refreshService.createTokens("Bot"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void updateTokensTest() {
        final User user = userService.saveUser(TestDataProvider.buildAlex());
        usersId.add(user.getUserId());
        final AuthResponse tokens = refreshService.updateTokens(refreshService.createTokens(user.getLogin())
                .getRefreshToken());
        assertThat(jdbcTemplate.queryForObject("select exists(select * from t_refresh where refresh = ?)",
                Boolean.class, tokens.getRefreshToken())).isTrue();
    }

    @Test
    void updateTokenNotFoundExcTest() {
        assertThatThrownBy(() -> refreshService.updateTokens(UUID.randomUUID().toString()))
                .isInstanceOf(TokenNotFoundException.class)
                .hasMessage("Токен не найден");
    }

    @Test
    void updateTokenInvalidExcTest() {
        final User user = userService.saveUser(TestDataProvider.buildAlex());
        usersId.add(user.getUserId());
        final AuthResponse tokens = refreshService.createTokens(user.getLogin());
        jdbcTemplate.update("update t_refresh set date = 0 where refresh = ?", tokens.getRefreshToken());
        assertThatThrownBy(() -> refreshService.updateTokens(tokens.getRefreshToken()))
                .isInstanceOf(RefreshTokenInvalidException.class)
                .hasMessage("Токен истек");
        assertThat(jdbcTemplate.queryForObject("select exists(select * from t_refresh where refresh = ?)",
                Boolean.class, tokens.getRefreshToken())).isFalse();
    }
}
