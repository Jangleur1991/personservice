package com.person.personservice;

import com.person.personservice.config.TestConfig;
import org.springframework.boot.SpringApplication;

public class TestPersonServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(PersonServiceApplication::main).with(TestConfig.class).run(args);
	}

}
