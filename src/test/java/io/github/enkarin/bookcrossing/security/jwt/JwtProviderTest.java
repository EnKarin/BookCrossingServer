package io.github.enkarin.bookcrossing.security.jwt;

import io.github.enkarin.bookcrossing.configuration.TimeSettings;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class JwtProviderTest extends BookCrossingBaseTests {

    @Autowired
    private JwtProvider provider;
    @Autowired
    private TimeSettings timeSettings;

    @Test
    void validateTokenShouldFailWithTokenExpired(final CapturedOutput output) {
        try {
            timeSettings.setClock(Clock.fixed(Instant.ofEpochSecond(0), ZoneOffset.ofHours(4)));
            final String token = provider.generateToken("Alice");
            assertThat(token)
                    .isEqualTo("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBbGljZSIsImV4cCI6OTAwfQ.JbQvgeEb-nrQtYAj7DDkwN2nS2JIxTHAr5MAWhrrU30");

            timeSettings.setClock(Clock.system(ZoneOffset.ofHours(7)));
            assertThat(provider.validateToken(token))
                    .isFalse();
            assertThat(output)
                    .contains("ERROR")
                    .contains("i.g.e.b.security.jwt.JwtProvider")
                    .contains("Token expired");
        } finally {
            timeSettings.setClock(Clock.system(ZoneOffset.ofHours(7)));
        }
    }

    @Test
    void validateTokenShouldFailWithTokenInvalid(final CapturedOutput output) {
        assertThat(provider.validateToken("token"))
                .isFalse();
        assertThat(output)
                .contains("ERROR")
                .contains("i.g.e.b.security.jwt.JwtProvider")
                .contains("invalid token");
    }
}
