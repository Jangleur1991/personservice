package com.person.personservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class PersonServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonServiceApplication.class, args);
	}

}
