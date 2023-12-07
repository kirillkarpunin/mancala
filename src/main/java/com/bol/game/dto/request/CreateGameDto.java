package com.bol.game.dto.request;

import jakarta.validation.constraints.Min;

public record CreateGameDto(
        @Min(1) int pitsPerPlayer,
        @Min(1) int stonesPerSpace,
        boolean isStealingAllowed,
        boolean isMultipleTurnAllowed
) {
}
