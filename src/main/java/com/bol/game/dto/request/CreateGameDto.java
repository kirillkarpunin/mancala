package com.bol.game.dto.request;

public record CreateGameDto(
        int pitsPerPlayer,
        int stonesPerSpace,
        boolean isStealingAllowed,
        boolean isMultipleTurnAllowed
) {
}
