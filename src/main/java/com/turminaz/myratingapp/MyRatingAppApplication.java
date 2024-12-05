package com.turminaz.myratingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class MyRatingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyRatingAppApplication.class, args);
	}

}
