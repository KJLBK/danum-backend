package com.danum.danum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DanumApplication {

	public static void main(String[] args) {
		SpringApplication.run(DanumApplication.class, args);
	}

}
