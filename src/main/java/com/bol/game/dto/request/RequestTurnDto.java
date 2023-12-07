package com.bol.game.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RequestTurnDto(
        @NotNull UUID userId,
        @Min(0) int spaceIndex
) {
}
