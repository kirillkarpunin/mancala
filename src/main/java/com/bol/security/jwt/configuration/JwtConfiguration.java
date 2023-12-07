package com.bol.security.jwt.configuration;


import com.bol.security.jwt.service.JwtService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtConfiguration {

    @Value("${security.jwt.signature.secret}")
    private String secret;

    @Value("${security.jwt.signature.algorithm}")
    private String algorithm;

    @Value("${security.jwt.expiresInSec}")
    private Long expiresInSec;

    @Bean
    public JwtService jwtService() {
        return new JwtService(jwtEncoder(), algorithm, expiresInSec);
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(secretKey())
                .build();
    }

    @Bean
    public SecretKey secretKey() {
        return new SecretKeySpec(secret.getBytes(), algorithm);
    }
}
