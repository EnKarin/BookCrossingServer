package ru.bookcrossing.BookcrossingServer.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.bookcrossing.BookcrossingServer.handlers.AuthenticationEntryPointHandler;
import ru.bookcrossing.BookcrossingServer.security.jwt.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private JwtFilter jwtFilter;

    private AuthenticationEntryPointHandler handler;

    @Autowired
    private void setJwtFilter(JwtFilter filter){
        jwtFilter = filter;
    }

    @Autowired
    private void setAuthenticationEntryPointHandler(AuthenticationEntryPointHandler hand){
        handler = hand;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .requiresChannel().antMatchers().requiresSecure()
                .and()
                .httpBasic().disable()
                .csrf().disable()
//                работает только для браузеров
//
//                .csrf()
//                .and()
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
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
