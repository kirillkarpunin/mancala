package com.bol.game.dto.response;

import com.bol.game.engine.model.GameStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GameDto(
        @NotNull UUID id,
        @NotNull GameStatus status
) {
}
