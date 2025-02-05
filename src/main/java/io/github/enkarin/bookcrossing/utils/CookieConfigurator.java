package io.github.enkarin.bookcrossing.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

@UtilityClass
public class CookieConfigurator {
    public static String configureRefreshTokenCookie(final String refreshTokenValue) {
        return ResponseCookie.from("refresh-token", refreshTokenValue)
            .httpOnly(true)
            .sameSite("None")
            .secure(true)
            .maxAge(Duration.ofDays(3))
            .build().toString();
    }
}
