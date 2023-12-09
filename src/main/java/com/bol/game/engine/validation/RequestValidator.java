package com.bol.game.engine.validation;

import com.bol.game.engine.model.GameState;

public interface RequestValidator {
    void validateCreateGameRequest(int pitsPerPlayer, int stonesPerPit);

    void validateTurnRequest(int playerIndex, int spaceIndex, GameState game);

    void validateInitializeGameRequest(GameState game, int numberOfPlayers);
}
