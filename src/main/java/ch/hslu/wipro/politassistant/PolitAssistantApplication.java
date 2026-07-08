package ch.hslu.wipro.politassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PolitAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolitAssistantApplication.class, args);
	}

}
