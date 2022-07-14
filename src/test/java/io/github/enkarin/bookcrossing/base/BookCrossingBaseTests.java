package io.github.enkarin.bookcrossing.base;

import io.github.enkarin.bookcrossing.init.MySQLInitializer;
import io.github.enkarin.bookcrossing.support.TestDataProvider;
import io.github.enkarin.bookcrossing.user.dto.UserDto;
import io.github.enkarin.bookcrossing.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@ContextConfiguration(initializers = MySQLInitializer.class)
public abstract class BookCrossingBaseTests {

    protected final List<Integer> usersId = new ArrayList<>(2);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected UserService userService;

    protected UserDto createAndSaveUser() {
        final UserDto user = userService.saveUser(TestDataProvider.buildAlex());
        usersId.add(user.getUserId());
        return user;
    }

    @AfterEach
    void delete() {
        usersId.forEach(u -> userService.deleteUser(u));
        usersId.clear();
    }
}
