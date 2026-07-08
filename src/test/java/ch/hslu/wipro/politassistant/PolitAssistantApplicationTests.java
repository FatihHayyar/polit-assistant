package ch.hslu.wipro.politassistant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PolitAssistantApplicationTests {

	@Test
	void contextLoads() {
	}

}
