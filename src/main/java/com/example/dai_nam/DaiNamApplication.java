package com.example.dai_nam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.dai_nam")
public class DaiNamApplication {

	public static void main(String[] args) {
		SpringApplication.run(DaiNamApplication.class, args);
	}

}
