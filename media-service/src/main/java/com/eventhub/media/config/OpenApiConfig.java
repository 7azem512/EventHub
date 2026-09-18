package com.eventhub.media.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mediaOpenAPI() {

        String authorizationUrl =
                "http://localhost:8083/realms/eventhub/protocol/openid-connect/auth";

        String tokenUrl =
                "http://localhost:8083/realms/eventhub/protocol/openid-connect/token";

        OAuthFlow authorizationCode = new OAuthFlow()
                .authorizationUrl(authorizationUrl)
                .tokenUrl(tokenUrl)
                .scopes(
                        new Scopes()
                                .addString("openid", "OpenID Connect")
                                .addString("profile", "User profile")
                                .addString("email", "User email")
                );

        SecurityScheme securityScheme =
                new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .flows(
                                new OAuthFlows()
                                        .authorizationCode(authorizationCode)
                        );

        return new OpenAPI()
                .info(
                        new Info()
                                .title("EventHub Media Service API")
                                .version("1.0")
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "keycloak",
                                        securityScheme
                                )
                )
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("keycloak")
                );
    }
}