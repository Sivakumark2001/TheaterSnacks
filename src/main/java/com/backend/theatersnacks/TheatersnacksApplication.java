package com.backend.theatersnacks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class TheatersnacksApplication {

	public static void main(String[] args) {
		// Ensure a Postgres-compatible zone ID is used (Asia/Kolkata instead of legacy Asia/Calcutta)
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		SpringApplication.run(TheatersnacksApplication.class, args);
	}

}
