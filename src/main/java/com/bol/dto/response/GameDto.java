package com.bol.dto.response;

import com.bol.engine.model.GameStatus;

import java.util.UUID;

public record GameDto(UUID id, GameStatus status) {
}
