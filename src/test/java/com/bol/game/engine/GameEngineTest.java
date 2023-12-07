package com.bol.game.engine;

import com.bol.game.engine.GameEngine;
import com.bol.game.engine.exception.GameEngineException;
import com.bol.game.engine.model.GameConfiguration;
import com.bol.game.engine.model.GameStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    @ParameterizedTest(name = "should fail when request turn for {0} game")
    @EnumSource(value = GameStatus.class, names = "ACTIVE", mode = EnumSource.Mode.EXCLUDE)
    public void shouldFailWhenRequestTurnForNotActiveGame(GameStatus status) {
        var engine = new GameEngine();
        var game = prepareGame(engine, 5, 5, true, true);

        game.setStatus(status);
        assertThrows(
                GameEngineException.class,
                () -> engine.turn(0, 0, game)
        );
    }


    @ParameterizedTest(name = "should fail when request turn by player # {0}")
    @ValueSource(ints = {-1, 1, 2})
    public void shouldFailWhenRequestTurnByWrongPlayer(int playerIndex) {
        var engine = new GameEngine();
        var game = prepareGame(engine, 5, 5, true, true);
        game.setStatus(GameStatus.ACTIVE);
        assertThrows(
                GameEngineException.class,
                () -> engine.turn(playerIndex, 0, game)
        );
    }

    @ParameterizedTest(name = "should fail when request turn with space index {0}")
    @ValueSource(ints = {-1, 3, 4, 5, 6, 7, 8})
    public void shouldFailWhenRequestTurnWithInvalidSpaceIndex(int spaceIndex) {
        var engine = new GameEngine();
        var game = prepareGame(engine, 3, 5, true, true);
        game.setStatus(GameStatus.ACTIVE);
        assertThrows(
                GameEngineException.class,
                () -> engine.turn(0, spaceIndex, game)
        );
    }

    @Test
    public void shouldFailWhenRequestTurnWithEmptyPitSpace() {
        var engine = new GameEngine();
        var game = prepareGame(engine, 3, 5, true, true);
        game.setStatus(GameStatus.ACTIVE);

        var spaceIndex = 0;
        game.getBoard()[spaceIndex] = 0;

        assertThrows(
                GameEngineException.class,
                () -> engine.turn(0, spaceIndex, game)
        );
    }

    @Test
    public void shouldSowStones() {
        var engine = new GameEngine();
        var game = prepareGame(engine, 4, 6, true, true);
        game.setStatus(GameStatus.ACTIVE);

        engine.turn(0, 1, game);

        assertArrayEquals(new int[]{6, 0, 7, 7, 1, 7, 7, 7, 6, 0}, game.getBoard());
        assertEquals(1, game.getCurrentPlayerIndex());
    }

    @Test
    public void shouldSkipOtherPlayerStoreWhenSowStones() {
        var engine = new GameEngine();
        var game = prepareGame(engine, 4, 6, true, true);
        game.setStatus(GameStatus.ACTIVE);

        engine.turn(0, 3, game);

        assertArrayEquals(new int[]{7, 6, 6, 0, 1, 7, 7, 7, 7, 0}, game.getBoard());
        assertEquals(1, game.getCurrentPlayerIndex());
    }

    @Test
    public void shouldStealWhenFinishTurnInOwnEmptyPit() {
        var engine = new GameEngine();
        var game = prepareGame(engine, 4, 6, true, true);
        game.setStatus(GameStatus.ACTIVE);
        game.getBoard()[0] = 0;

        engine.turn(0, 3, game);

        assertArrayEquals(new int[]{0, 6, 6, 0, 9, 7, 7, 7, 0, 0}, game.getBoard());
        assertEquals(1, game.getCurrentPlayerIndex());
    }

    @Test
    public void shouldNotStealWhenItIsNotAllowed() {
        var engine = new GameEngine();
        var game = prepareGame(engine, 4, 6, false, true);
        game.setStatus(GameStatus.ACTIVE);
        game.getBoard()[0] = 0;

        engine.turn(0, 3, game);

        assertArrayEquals(new int[]{1, 6, 6, 0, 1, 7, 7, 7, 7, 0}, game.getBoard());
        assertEquals(1, game.getCurrentPlayerIndex());
    }

    @Test
    public void shouldNotStealWhenFinishTurnInOtherPlayerEmptyPit() {
        var engine = new GameEngine();
        var game = prepareGame(engine, 4, 6, true, true);
        game.setStatus(GameStatus.ACTIVE);
        game.getBoard()[6] = 0;

        engine.turn(0, 0, game);

        assertArrayEquals(new int[]{0, 7, 7, 7, 1, 7, 1, 6, 6, 0}, game.getBoard());
        assertEquals(1, game.getCurrentPlayerIndex());
    }

    @Test
    public void shouldFinishGameWhenOwnSpaceRowIsEmpty() {
        var engine = new GameEngine();
        var game = prepareGame(engine, 4, 4, true, true);
        game.setStatus(GameStatus.ACTIVE);
        game.getBoard()[0] = 0;
        game.getBoard()[1] = 0;
        game.getBoard()[2] = 0;

        engine.turn(0, 3, game);

        assertArrayEquals(new int[]{0, 0, 0, 0, 1, 0, 0, 0, 0, 19}, game.getBoard());
        assertEquals(0, game.getCurrentPlayerIndex());
        assertEquals(GameStatus.FINISHED, game.getStatus());
        assertEquals(1, game.getWinnerIndex());
    }

    @Test
    public void shouldFinishGameWhenOtherSpaceRowIsEmpty() {
        var engine = new GameEngine();
        var game = prepareGame(engine, 4, 4, true, true);
        game.setStatus(GameStatus.ACTIVE);
        game.getBoard()[2] = 1;
        game.getBoard()[3] = 0;
        game.getBoard()[6] = 0;
        game.getBoard()[7] = 0;
        game.getBoard()[8] = 0;
        game.getBoard()[9] = 50;

        engine.turn(0, 2, game);

        assertArrayEquals(new int[]{0, 0, 0, 0, 13, 0, 0, 0, 0, 50}, game.getBoard());
        assertEquals(0, game.getCurrentPlayerIndex());
        assertEquals(GameStatus.FINISHED, game.getStatus());
        assertEquals(1, game.getWinnerIndex());
    }

    @Test
    public void shouldHaveAnotherTurnWhenFinishInOwnStore() {
        var engine = new GameEngine();
        var game = prepareGame(engine, 4, 4, true, true);
        game.setStatus(GameStatus.ACTIVE);

        engine.turn(0, 0, game);

        assertArrayEquals(new int[]{0, 5, 5, 5, 1, 4, 4, 4, 4, 0}, game.getBoard());
        assertEquals(0, game.getCurrentPlayerIndex());
        assertEquals(GameStatus.ACTIVE, game.getStatus());
    }

    @Test
    public void shouldNowHaveAnotherTurnWhenMultipleTurnIsNotAllowed() {
        var engine = new GameEngine();
        var game = prepareGame(engine, 4, 4, true, false);
        game.setStatus(GameStatus.ACTIVE);

        engine.turn(0, 0, game);

        assertArrayEquals(new int[]{0, 5, 5, 5, 1, 4, 4, 4, 4, 0}, game.getBoard());
        assertEquals(1, game.getCurrentPlayerIndex());
        assertEquals(GameStatus.ACTIVE, game.getStatus());
    }

    private static GameConfiguration prepareGame(GameEngine engine, int pitsPerPlayer, int stonesPerPit, boolean isStealingAllowed, boolean isMultipleTurnAllowed) {
        var game = engine.createGame(UUID.randomUUID(), pitsPerPlayer, stonesPerPit, isStealingAllowed, isMultipleTurnAllowed);
        game.addPlayer(UUID.randomUUID());
        game.initialize();

        return game;
    }
}