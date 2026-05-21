package org.informatics.winterolympics.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null) {
            return authorities;
        }

        Object springApiAccessObject = resourceAccess.get("spring-api");

        if (!(springApiAccessObject instanceof Map<?, ?> springApiAccess)) {
            return authorities;
        }

        Object rolesObject = springApiAccess.get("roles");

        if (!(rolesObject instanceof List<?> roles)) {
            return authorities;
        }

        for (Object roleObject : roles) {
            String role = roleObject.toString();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        return authorities;
    }
}