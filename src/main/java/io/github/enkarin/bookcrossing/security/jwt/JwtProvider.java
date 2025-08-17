package io.github.enkarin.bookcrossing.security.jwt;

import io.github.enkarin.bookcrossing.configuration.TimeSettings;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {
    private final TimeSettings timeSettings;

    @Value("${jwt.secret}")
    private String jwtSecretBase64;

    @SuppressWarnings("PMD.ReplaceJavaUtilDate") // TODO FIXME
    public String generateToken(final String login) {
        final Date date = Date.from(timeSettings.dateTimeNow().plusMinutes(15).toInstant(timeSettings.offset()));
        final byte[] keyBytes = Decoders.BASE64.decode(jwtSecretBase64);
        final SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.builder()
                .subject(login)
                .expiration(date)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(final String token) {
        try {
            final byte[] keyBytes = Decoders.BASE64.decode(jwtSecretBase64);
            final SecretKey key = Keys.hmacShaKeyFor(keyBytes);
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired");
        } catch (Exception e) {
            log.error("invalid token");
        }
        return false;
    }

    public String getLoginFromToken(final String token) {
        final byte[] keyBytes = Decoders.BASE64.decode(jwtSecretBase64);
        final SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
