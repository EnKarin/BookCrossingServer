package io.github.enkarin.bookcrossing.security;

import io.github.enkarin.bookcrossing.handlers.AuthenticationEntryPointHandler;
import io.github.enkarin.bookcrossing.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationEntryPointHandler handler;

    @SneakyThrows
    @Bean
    protected SecurityFilterChain filterChain(final HttpSecurity httpSecurity) {
        httpSecurity
                .requiresChannel().antMatchers().requiresSecure()
                .and()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/adm/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole("USER")
                .antMatchers("/books/**").permitAll()
                .antMatchers("/register", "/auth", "/refresh").permitAll()
                .and()
                .exceptionHandling().authenticationEntryPoint(handler)
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
