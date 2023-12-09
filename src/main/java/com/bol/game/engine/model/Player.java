package com.bol.game.engine.model;

import java.util.UUID;

public record Player(UUID userId, int playerIndex, SpaceRange spaceRange) {
}
