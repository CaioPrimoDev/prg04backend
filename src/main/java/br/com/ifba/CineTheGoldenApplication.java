package br.com.ifba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CineTheGoldenApplication {

	public static void main(String[] args) {

		SpringApplication.run(CineTheGoldenApplication.class, args);
	}

}
