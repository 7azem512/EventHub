package com.eventhub.event.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                new KeycloakRealmRoleConverter()
        );

        converter.setPrincipalClaimName(
                "preferred_username"
        );

        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/error"
                        )
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/events/**"
                        )
                        .hasAnyRole(
                                "USER",
                                "ORGANIZER",
                                "ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/events/**"
                        )
                        .hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/events/**"
                        )
                        .hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/events/**"
                        )
                        .hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/events/**"
                        )
                        .hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categories/**"
                        )
                        .hasAnyRole(
                                "USER",
                                "ORGANIZER",
                                "ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/categories/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/categories/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/categories/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers("/api/**")
                        .authenticated()

                        .anyRequest()
                        .denyAll()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                );

        return http.build();
    }
}