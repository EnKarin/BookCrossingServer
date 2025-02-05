package io.github.enkarin.bookcrossing.configuration;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieConfigurator {
    public String configureRefreshTokenCookie(final String refreshTokenValue) {
        return ResponseCookie.from("refresh-token", refreshTokenValue)
            .httpOnly(true)
            .sameSite("None")
            .secure(true)
            .maxAge(Duration.ofDays(3))
            .build().toString();
    }
}
