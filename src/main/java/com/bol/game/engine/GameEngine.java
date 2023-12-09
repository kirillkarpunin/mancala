package com.bol.game.engine;

import com.bol.game.engine.model.GameState;

import java.util.UUID;

public interface GameEngine {

    GameState createGame(
            int pitsPerPlayer, int stonesPerPit, boolean isStealingAllowed, boolean isMultipleTurnAllowed
    );

    void addPlayer(UUID userId, GameState game);

    void turn(int playerIndex, int spaceIndex, GameState game);

    void initialize(GameState game);
}
