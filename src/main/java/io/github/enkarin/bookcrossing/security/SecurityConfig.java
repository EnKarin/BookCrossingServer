package io.github.enkarin.bookcrossing.security;

import io.github.enkarin.bookcrossing.handlers.AuthenticationEntryPointHandler;
import io.github.enkarin.bookcrossing.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final AuthenticationEntryPointHandler handler;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @SneakyThrows
    @Bean
    protected SecurityFilterChain filterChain(final HttpSecurity httpSecurity) {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type", "userId", "firstUserId", "secondUserId"));
        corsConfiguration.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));

        httpSecurity
            .requiresChannel().antMatchers().requiresSecure()
            .and()
            .httpBasic().disable()
            .csrf().disable()
            .cors().configurationSource(request -> corsConfiguration)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/adm/**").hasRole("ADMIN")
            .antMatchers("/user/**").hasRole("USER")
            .antMatchers("/books/**").permitAll()
            .antMatchers("/utils/**").permitAll()
            .antMatchers("/register", "/auth", "/refresh", "/genre").permitAll()
            .and()
            .exceptionHandling().authenticationEntryPoint(handler)
            .and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
