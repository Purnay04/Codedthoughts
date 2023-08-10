package com.codedthoughts.codedthoughts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CodedthoughtsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodedthoughtsApplication.class, args);
	}

}
