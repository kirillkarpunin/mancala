package com.bol.game.engine;

import com.bol.game.engine.model.GameConfiguration;

import java.util.UUID;

public interface GameEngine {

    GameConfiguration createGameConfiguration(
            UUID userId, int pitsPerPlayer, int stonesPerPit, boolean isStealingAllowed, boolean isMultipleTurnAllowed
    );

    void turn(int playerIndex, int spaceIndex, GameConfiguration game);
}
