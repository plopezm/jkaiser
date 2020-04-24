package com.aeox.jkaiser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JkaiserApplication {

	public static void main(String[] args) {
		SpringApplication.run(JkaiserApplication.class, args);
	}

}
