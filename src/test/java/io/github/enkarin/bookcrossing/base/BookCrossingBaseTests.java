package io.github.enkarin.bookcrossing.base;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.github.enkarin.bookcrossing.init.MySQLInitializer;
import io.github.enkarin.bookcrossing.registation.dto.UserRegistrationDto;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import io.github.enkarin.bookcrossing.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(initializers = MySQLInitializer.class)
public abstract class BookCrossingBaseTests {

    protected final List<Integer> usersId = new ArrayList<>(2);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected WebTestClient webClient;

    @Autowired
    protected UserService userService;

    @RegisterExtension
    protected static final GreenMailExtension GREEN_MAIL = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withDisabledAuthentication())
            .withPerMethodLifecycle(false);

    protected UserDto createAndSaveUser(final UserRegistrationDto userRegistrationDto) {
        final UserDto user = userService.saveUser(userRegistrationDto);
        usersId.add(user.getUserId());
        return user;
    }

    @AfterEach
    void delete() {
        usersId.forEach(u -> userService.deleteUser(u));
        usersId.clear();
    }
}
