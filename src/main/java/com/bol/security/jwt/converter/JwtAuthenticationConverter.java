package com.bol.security.jwt.converter;

import com.bol.security.jwt.JwtAuthenticationToken;
import com.bol.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserService userService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        var claim = Optional.ofNullable(jwt.getClaimAsString(JwtClaimNames.SUB))
                .orElseThrow(() -> new BadJwtException("JWT missing %s claim".formatted(JwtClaimNames.SUB)));

        var userId = parseUserId(claim);
        if (!userService.isUserExists(userId)) {
            throw new BadJwtException("User doesn't exist: userId=%s".formatted(userId));
        }

        return new JwtAuthenticationToken(userId, jwt);
    }

    private static UUID parseUserId(String claim) {
        try {
            return UUID.fromString(claim);
        } catch (Exception exception) {
            throw new BadJwtException("Can't parse userId, expecting UUID format: userId=%s".formatted(claim));
        }
    }
}
