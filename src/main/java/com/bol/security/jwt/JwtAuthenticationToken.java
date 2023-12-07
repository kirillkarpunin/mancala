package com.bol.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final UUID userId;
    private final Jwt token;

    public JwtAuthenticationToken(UUID userId, Jwt token) {
        super(null);
        setAuthenticated(true);
        this.userId = userId;
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }
}
