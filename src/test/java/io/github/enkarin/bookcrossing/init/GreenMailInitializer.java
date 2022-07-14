package io.github.enkarin.bookcrossing.init;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class GreenMailInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final int SMTP_PORT = 3025;
    private static final DockerImageName IMAGE = DockerImageName.parse("greenmail/standalone:1.6.9");
    private static final GenericContainer<?> GREEN_MAIL_CONTAINER = new GenericContainer<>(IMAGE);

    static {
        GREEN_MAIL_CONTAINER
                .waitingFor(Wait.forLogMessage(".*Starting GreenMail standalone.*", 1))
                .withEnv("GREENMAIL_OPTS", "-Dgreenmail.setup.test.smtp -Dgreenmail.hostname=0.0.0.0 " +
                        "-Dgreenmail.auth.disabled")
                .withExposedPorts(SMTP_PORT)
                .start();
    }

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                "spring.mail.host=" + GREEN_MAIL_CONTAINER.getHost(),
                "spring.mail.port=" + GREEN_MAIL_CONTAINER.getMappedPort(SMTP_PORT)
        ).applyTo(applicationContext.getEnvironment());
    }

    public static String getMailHost() {
        return GREEN_MAIL_CONTAINER.getHost();
    }

    public static int getMappedSmtpPort() {
        return GREEN_MAIL_CONTAINER.getMappedPort(SMTP_PORT);
    }
}
