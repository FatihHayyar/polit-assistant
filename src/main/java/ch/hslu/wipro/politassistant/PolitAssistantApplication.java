package ch.hslu.wipro.politassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import ch.hslu.wipro.politassistant.config.ClassificationRulesProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
@EnableScheduling
@EnableConfigurationProperties(ClassificationRulesProperties.class)
@SpringBootApplication
public class PolitAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolitAssistantApplication.class, args);
	}

}
