package com.example.cowork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.example.cowork")
public class CoworkApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoworkApplication.class, args);
	}

}
