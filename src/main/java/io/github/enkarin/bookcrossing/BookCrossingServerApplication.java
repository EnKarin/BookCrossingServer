package io.github.enkarin.bookcrossing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookCrossingServerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(BookCrossingServerApplication.class, args);
    }
}



