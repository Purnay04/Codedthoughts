package com.codedthoughts.codedthoughts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://127.0.0.1:5173/")
@SpringBootApplication
@EnableJpaRepositories
public class CodedthoughtsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodedthoughtsApplication.class, args);
	}

}
