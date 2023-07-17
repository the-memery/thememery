package com.quest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
// @SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
// @ComponentScan("etna.config")
// @EntityScan("etna.model")
// @EnableJpaRepositories("etna.repositories")
public class BackJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackJavaApplication.class, args);
	}

}
