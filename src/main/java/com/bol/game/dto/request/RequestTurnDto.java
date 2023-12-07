package com.bol.game.dto.request;

import java.util.UUID;

public record RequestTurnDto(UUID userId, int spaceIndex) {
}
