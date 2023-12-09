package com.bol.game.engine;

import com.bol.game.engine.model.GameConfiguration;

import java.util.UUID;

public interface GameEngine {

    GameConfiguration createGameConfiguration(
            int pitsPerPlayer, int stonesPerPit, boolean isStealingAllowed, boolean isMultipleTurnAllowed
    );

    void addPlayer(UUID userId, GameConfiguration game);

    void turn(int playerIndex, int spaceIndex, GameConfiguration game);

    void initialize(GameConfiguration game);
}
