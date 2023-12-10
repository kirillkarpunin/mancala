package com.bol.game.dto.request;

import jakarta.validation.constraints.Min;

public record RequestTurnDto(
        @Min(0) int spaceIndex
) {
}
