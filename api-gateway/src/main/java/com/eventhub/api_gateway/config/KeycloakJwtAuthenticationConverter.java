package com.eventhub.api_gateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KeycloakJwtAuthenticationConverter
        implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {

        List<GrantedAuthority> authorities = new ArrayList<>();

        Map<String, Object> realmAccess =
                jwt.getClaim("realm_access");

        if (realmAccess != null) {

            Object rolesObject =
                    realmAccess.get("roles");

            if (rolesObject instanceof Collection<?> roles) {

                roles.forEach(role ->
                        authorities.add(
                                new SimpleGrantedAuthority(
                                        "ROLE_" + role
                                )
                        )
                );
            }
        }

        String username =
                jwt.getClaimAsString("preferred_username");

        if (username == null) {
            username = jwt.getSubject();
        }

        return Mono.just(
                new JwtAuthenticationToken(
                        jwt,
                        authorities,
                        username
                )
        );
    }
}