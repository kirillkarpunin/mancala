package com.bol.game.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateGameDto(
        @NotNull @Min(1) Integer pitsPerPlayer,
        @NotNull @Min(1) Integer stonesPerPit,
        @NotNull Boolean isStealingAllowed,
        @NotNull Boolean isMultipleTurnAllowed
) {
}
