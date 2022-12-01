package io.github.enkarin.bookcrossing.support;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class MySQLInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final DockerImageName IMAGE = DockerImageName.parse("mysql:8.0.28");
    private static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>(IMAGE);

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        MY_SQL_CONTAINER
                .withUsername("root")
                .withPassword("root")
                .withDatabaseName("bookcrossing")
                .start();
        TestPropertyValues.of(
                "spring.datasource.url=" + MY_SQL_CONTAINER.getJdbcUrl(),
                "spring.datasource.username=" + MY_SQL_CONTAINER.getUsername(),
                "spring.datasource.password=" + MY_SQL_CONTAINER.getPassword(),
                "flyway.user=", MY_SQL_CONTAINER.getUsername(),
                "flyway.password=", MY_SQL_CONTAINER.getPassword()
        ).applyTo(applicationContext.getEnvironment());
    }
}
