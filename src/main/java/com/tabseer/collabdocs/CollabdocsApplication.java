package com.tabseer.collabdocs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CollabdocsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollabdocsApplication.class, args);
	}

}
