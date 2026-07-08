package ch.hslu.wipro.politassistant;

import org.springframework.boot.SpringApplication;

public class TestPolitAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.from(PolitAssistantApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
