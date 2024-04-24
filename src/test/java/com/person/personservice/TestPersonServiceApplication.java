package com.person.personservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestPersonServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(PersonServiceApplication::main).with(TestPersonServiceApplication.class).run(args);
	}

}
