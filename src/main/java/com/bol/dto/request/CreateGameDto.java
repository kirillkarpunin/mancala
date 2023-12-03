package com.bol.dto.request;

import java.util.UUID;

// TODO: Enable Spring Security and get userId from token
public record CreateGameDto(
        UUID userId,
        int pitsPerPlayer,
        int stonesPerSpace,
        boolean isStealingAllowed,
        boolean isMultipleTurnAllowed
) {
}
