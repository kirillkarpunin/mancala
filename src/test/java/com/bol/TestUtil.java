package com.bol;

import com.bol.game.engine.GameEngine;
import com.bol.game.engine.GameEngineImpl;
import com.bol.game.engine.model.GameState;
import com.bol.game.engine.validation.RequestValidator;
import org.mockito.Mockito;

import java.util.UUID;

public class TestUtil {

    public static GameState prepareGame(int pitsPerPlayer, int stonesPerPit) {
        return prepareGame(pitsPerPlayer, stonesPerPit, true, true);
    }

    public static GameState prepareGame(int pitsPerPlayer, int stonesPerPit, boolean isStealingAllowed, boolean isMultipleTurnAllowed) {
        var engine = prepareEngine();
        return prepareGame(engine, pitsPerPlayer, stonesPerPit, isStealingAllowed, isMultipleTurnAllowed);
    }

    public static GameState prepareGame(GameEngine engine, int pitsPerPlayer, int stonesPerPit, boolean isStealingAllowed, boolean isMultipleTurnAllowed) {
        var game = createGame(engine, pitsPerPlayer, stonesPerPit, isStealingAllowed, isMultipleTurnAllowed);

        engine.addPlayer(UUID.randomUUID(), game);
        engine.addPlayer(UUID.randomUUID(), game);
        engine.initialize(game);

        return game;
    }

    public static GameEngine prepareEngine() {
        var validator = Mockito.mock(RequestValidator.class);
        Mockito.doNothing().when(validator).validateCreateGameRequest(Mockito.anyInt(), Mockito.anyInt());
        Mockito.doNothing().when(validator).validateTurnRequest(Mockito.anyInt(), Mockito.anyInt(), Mockito.any());

        return new GameEngineImpl(validator);
    }

    public static GameState createGame(int pitsPerPlayer, int stonesPerPit) {
        var engine = prepareEngine();
        return createGame(engine, pitsPerPlayer, stonesPerPit, true, true);
    }

    public static GameState createGame(GameEngine engine, int pitsPerPlayer, int stonesPerPit, boolean isStealingAllowed, boolean isMultipleTurnAllowed) {
        return engine.createGame(pitsPerPlayer, stonesPerPit, isStealingAllowed, isMultipleTurnAllowed);
    }
}
