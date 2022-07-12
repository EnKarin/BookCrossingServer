package io.github.enkarin.bookcrossing.base;

import io.github.enkarin.bookcrossing.init.GreenMailInitializer;
import io.github.enkarin.bookcrossing.init.MySQLInitializer;
import io.github.enkarin.bookcrossing.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@ContextConfiguration(initializers = {MySQLInitializer.class, GreenMailInitializer.class})
public abstract class BookCrossingBaseTests {

    protected final List<Integer> usersId = new ArrayList<>(2);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected WebTestClient webClient;

    @Autowired
    protected UserService userService;

    @AfterEach
    void delete() {
        usersId.forEach(u -> userService.deleteUser(u));
        usersId.clear();
    }
}
