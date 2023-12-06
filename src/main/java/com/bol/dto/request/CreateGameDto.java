package com.bol.dto.request;

public record CreateGameDto(
        int pitsPerPlayer,
        int stonesPerSpace,
        boolean isStealingAllowed,
        boolean isMultipleTurnAllowed
) {
}
