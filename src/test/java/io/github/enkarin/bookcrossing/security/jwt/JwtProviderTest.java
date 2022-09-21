package io.github.enkarin.bookcrossing.security.jwt;

import io.github.enkarin.bookcrossing.base.BookCrossingBaseTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class JwtProviderTest extends BookCrossingBaseTests {

    @Autowired
    private JwtProvider provider;

    @Test
    void validateTokenShouldFailWithTokenExpired(final CapturedOutput output) {
        assertThat(provider.validateToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBbGVrc2V5IiwiZXhwIjoxNjUyNjMxNTI4fQ.qdxyWmmjtybiwE8bgmyK9UTS5A-FgCfWTkNRanynhl69FaTqphzZR3Sy6GWAZqyKPP9MsiA0VBhsinYgDGdy5Q"))
                .isFalse();
        assertThat(output)
                .contains("ERROR")
                .contains("JwtProvider - Token expired");
    }

    @Test
    void validateTokenShouldFailWithTokenInvalid(final CapturedOutput output) {
        assertThat(provider.validateToken("token"))
                .isFalse();
        assertThat(output)
                .contains("ERROR")
                .contains("JwtProvider - invalid token");
    }
}
