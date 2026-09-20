package com.eventhub.booking.config;

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

    private static final String SECURITY_SCHEME_NAME = "keycloak";

    @Bean
    public OpenAPI bookingServiceOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("EventHub Booking Service API")
                        .description("Booking management APIs for EventHub")
                        .version("v1")
                )

                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(SECURITY_SCHEME_NAME)
                )

                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
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