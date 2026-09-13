package com.eventhub.event.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI eventServiceOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("EventHub Event Service API")
                                .version("v1")
                                .description(
                                        "Event management, categories and ticket types API"
                                )
                );
    }
}
