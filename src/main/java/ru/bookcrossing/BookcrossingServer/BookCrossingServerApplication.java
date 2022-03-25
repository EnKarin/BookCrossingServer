package ru.bookcrossing.BookcrossingServer;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookCrossingServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookCrossingServerApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}



