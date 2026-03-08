package com.technest_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TechnestApiApplication {

	public static void main(String[] args) {
		System.out.println("Working dir: " + System.getProperty("user.dir"));
		SpringApplication.run(TechnestApiApplication.class, args);
	}

}
