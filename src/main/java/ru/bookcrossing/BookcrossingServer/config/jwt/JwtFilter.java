package ru.bookcrossing.BookcrossingServer.config.jwt;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import ru.bookcrossing.BookcrossingServer.entity.User;
import ru.bookcrossing.BookcrossingServer.service.CustomUserDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.NoSuchElementException;

import static org.springframework.util.StringUtils.hasText;

@Component
@Log
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION = "Authorization";

    private JwtProvider jwtProvider;

    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private void setJwtProvider(JwtProvider provider){
        jwtProvider = provider;
    }

    @Autowired
    private void setCustomUserDetailsService(CustomUserDetailsService custService){
        customUserDetailsService = custService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String token = getTokenFromRequest((HttpServletRequest) servletRequest);
        if (token != null && jwtProvider.validateToken(token)) {
            try {
                String userLogin = jwtProvider.getLoginFromToken(token);
                User customUserDetails = customUserDetailsService.loadUserByUsername(userLogin);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUserDetails,
                        null, customUserDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }catch (NoSuchElementException ignored){
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
