package com.laboratory.url;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class UrlApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlApplication.class, args);
	}

}

