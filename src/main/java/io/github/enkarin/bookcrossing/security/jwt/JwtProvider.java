package io.github.enkarin.bookcrossing.security.jwt;

import io.github.enkarin.bookcrossing.configuration.TimeSettings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {
    private final TimeSettings timeSettings;

    @Value("$(jwt.secret)")
    private String jwtSecret;

    public String generateToken(final String login) {
        final Date date = Date.from(timeSettings.dateTimeNow().plusMinutes(15).toInstant(timeSettings.offset()));
        return Jwts.builder()
                .setSubject(login)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateToken(final String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired");
        } catch (Exception e) {
            log.error("invalid token");
        }
        return false;
    }

    public String getLoginFromToken(final String token) {
        final Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}
