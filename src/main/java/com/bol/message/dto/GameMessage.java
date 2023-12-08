package com.bol.message.dto;

import com.bol.game.engine.model.GameStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GameMessage(
        @NotNull UUID id,
        @NotNull GameStatus status
) {
}