package com.bol.game.engine.validation;

import com.bol.game.engine.model.GameConfiguration;

public interface RequestValidator {
    void validateCreateGameRequest(int pitsPerPlayer, int stonesPerPit);

    void validateTurnRequest(int playerIndex, int spaceIndex, GameConfiguration game);

    void validateInitializeGameRequest(GameConfiguration game, int numberOfPlayers);
}
