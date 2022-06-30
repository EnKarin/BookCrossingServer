package io.github.enkarin.bookcrossing;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class BookCrossingBaseTests {

    @Container
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer("mysql:8.0.28")
            .withUsername("root")
            .withPassword("root")
            .withDatabaseName("bookcrossing");

    @DynamicPropertySource
    public static void configure(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("flyway.user", MY_SQL_CONTAINER::getUsername);
        registry.add("flyway.password", MY_SQL_CONTAINER::getPassword);
    }
}
