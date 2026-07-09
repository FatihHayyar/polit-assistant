package ch.hslu.wipro.politassistant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Polit Assistant API")
                        .version("1.0")
                        .description("OpenParlData based political monitoring backend"));
    }
}