package com.bol.message.dto;

import com.bol.game.engine.model.GameStatus;
import com.bol.game.engine.model.Player;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record GameMessage(
        @NotNull UUID id,
        @NotNull Integer pitsPerPlayer,
        @NotNull GameStatus status,
        @NotNull int[] board,
        @NotNull List<Player> players,
        @NotNull int currentPlayerIndex,
        @NotNull Integer winnerIndex
) {
}
