package com.bol.game.dto.response;

import com.bol.game.engine.model.GameStatus;

import java.util.UUID;

public record GameDto(UUID id, GameStatus status) {
}
