package com.bol.auth.service;


import com.bol.auth.model.User;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;

// TODO: Interface
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwsHeader jwsHeader;
    private final long jwtExpiresInSec;

    public JwtService(JwtEncoder jwtEncoder, String signatureAlgorithm, long jwtExpiresInSec) {
        this.jwtEncoder = jwtEncoder;
        this.jwsHeader = JwsHeader.with(() -> signatureAlgorithm).build();
        this.jwtExpiresInSec = jwtExpiresInSec;
    }

    public String generateToken(User user) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .subject(user.id().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(jwtExpiresInSec))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims))
                .getTokenValue();
    }
}
