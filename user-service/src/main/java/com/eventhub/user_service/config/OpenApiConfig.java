package com.eventhub.user_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String OAUTH_SCHEME = "keycloak";

    @Bean
    public OpenAPI userServiceOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("EventHub User Service API")
                                .version("v1")
                                .description("User profile management API")
                )

                .servers(
                        List.of(
                                new Server()
                                        .url("http://localhost:8081")
                                        .description("EventHub API Gateway")
                        )
                )

                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(OAUTH_SCHEME)
                )

                .components(
                        new Components()
                                .addSecuritySchemes(
                                        OAUTH_SCHEME,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.OAUTH2)
                                                .flows(
                                                        new OAuthFlows()
                                                                .authorizationCode(
                                                                        new OAuthFlow()
                                                                                .authorizationUrl(
                                                                                        "http://localhost:8083/realms/eventhub/protocol/openid-connect/auth"
                                                                                )
                                                                                .tokenUrl(
                                                                                        "http://localhost:8083/realms/eventhub/protocol/openid-connect/token"
                                                                                )
                                                                                .scopes(
                                                                                        new Scopes()
                                                                                                .addString("openid", "OpenID Connect")
                                                                                                .addString("profile", "User profile")
                                                                                                .addString("email", "User email")
                                                                                )
                                                                )
                                                )
                                )
                );
    }
}