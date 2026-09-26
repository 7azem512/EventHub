package com.eventhub.notification.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "keycloak";

    @Bean
    public OpenAPI notificationOpenAPI() {

        OAuthFlow authorizationCodeFlow = new OAuthFlow()
                .authorizationUrl(
                        "http://localhost:8083/realms/eventhub/protocol/openid-connect/auth"
                )
                .tokenUrl(
                        "http://localhost:8083/realms/eventhub/protocol/openid-connect/token"
                )
                .scopes(
                        new Scopes()
                                .addString("openid", "OpenID Connect")
                );

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(
                        new OAuthFlows()
                                .authorizationCode(authorizationCodeFlow)
                );

        Server gatewayServer = new Server()
                .url("http://localhost:8081")
                .description("API Gateway");

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Notification Service API")
                                .version("1.0")
                                .description("Notification APIs for EventHub")
                )
                .servers(List.of(gatewayServer))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        securityScheme
                                )
                )
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(SECURITY_SCHEME_NAME)
                );
    }
}