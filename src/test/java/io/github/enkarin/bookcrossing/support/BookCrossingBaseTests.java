package io.github.enkarin.bookcrossing.support;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.github.enkarin.bookcrossing.books.dto.BookModelDto;
import io.github.enkarin.bookcrossing.books.service.BookService;
import io.github.enkarin.bookcrossing.chat.repository.CorrespondenceRepository;
import io.github.enkarin.bookcrossing.registration.dto.LoginRequest;
import io.github.enkarin.bookcrossing.registration.dto.UserRegistrationDto;
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
@ContextConfiguration(initializers = PostgreSQLInitializer.class)
public abstract class BookCrossingBaseTests {

    private final List<Integer> usersId = new ArrayList<>(2);

    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected WebTestClient webClient;
    @Autowired
    protected UserService userService;
    @Autowired
    protected BookService bookService;
    @Autowired
    protected CorrespondenceRepository correspondenceRepository;

    @RegisterExtension
    protected static final GreenMailExtension GREEN_MAIL = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withDisabledAuthentication())
            .withPerMethodLifecycle(false);

    @AfterEach
    void delete() {
        correspondenceRepository.deleteAll();
        usersId.forEach(u -> userService.deleteUser(u));
        usersId.clear();
    }

    protected final UserDto createAndSaveUser(final UserRegistrationDto userRegistrationDto) {
        final UserDto user = userService.saveUser(userRegistrationDto);
        usersId.add(user.getUserId());
        return user;
    }

    protected final void trackUserId(final int userId) {
        usersId.add(userId);
    }

    protected void enabledUser(final int userId) {
        jdbcTemplate.update("update bookcrossing.t_user set enabled = true where user_id = " + userId);
    }

    protected String generateAccessToken(final LoginRequest request) {
        return userService.findByLoginAndPassword(request).getAccessToken();
    }

    protected List<Integer> createAndSaveBooks(final String user) {
        return TestDataProvider.buildBooks().stream()
                .map(b -> bookService.saveBook(b, user))
                .map(BookModelDto::getBookId)
                .toList();
    }
}
