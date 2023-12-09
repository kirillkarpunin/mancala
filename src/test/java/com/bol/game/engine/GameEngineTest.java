package com.bol.game.engine;

import com.bol.game.engine.model.GameStatus;
import com.bol.game.engine.util.GameEngineUtil;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.bol.TestUtil.prepareEngine;
import static com.bol.TestUtil.prepareGame;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GameEngineTest {

    @Test
    public void shouldInitializeGame() {
        var pitsPerPlayer = 3;
        var stonesPerPit = 4;

        var engine = prepareEngine();
        var game = engine.createGame(pitsPerPlayer, stonesPerPit, true, true);
        engine.addPlayer(UUID.randomUUID(), game);
        engine.addPlayer(UUID.randomUUID(), game);
        engine.initialize(game);

        var spaceRanges = GameEngineUtil.getPlayerSpaces(game);
        assertEquals(spaceRanges.size(), 2);

        var firstSpaceRange = spaceRanges.get(0);
        assertEquals(0, firstSpaceRange.firstPitIndex());
        assertEquals(2, firstSpaceRange.lastPitIndex());
        assertEquals(3, firstSpaceRange.storeIndex());

        var secondSpaceRange = spaceRanges.get(1);
        assertEquals(4, secondSpaceRange.firstPitIndex());
        assertEquals(6, secondSpaceRange.lastPitIndex());
        assertEquals(7, secondSpaceRange.storeIndex());

        var board = game.getBoard();
        assertArrayEquals(new int[]{4, 4, 4, 0, 4, 4, 4, 0}, board);
    }

    @Test
    public void shouldSowStones() {
        var engine = prepareEngine();
        var game = prepareGame(engine, 4, 6, true, true);

        engine.turn(0, 1, game);

        assertArrayEquals(new int[]{6, 0, 7, 7, 1, 7, 7, 7, 6, 0}, game.getBoard());
        assertEquals(1, game.getCurrentPlayerIndex());
    }

    @Test
    public void shouldSkipOtherPlayerStoreWhenSowStones() {
        var engine = prepareEngine();
        var game = prepareGame(engine, 4, 6, true, true);
        game.setStatus(GameStatus.ACTIVE);

        engine.turn(0, 3, game);

        assertArrayEquals(new int[]{7, 6, 6, 0, 1, 7, 7, 7, 7, 0}, game.getBoard());
        assertEquals(1, game.getCurrentPlayerIndex());
    }

    @Test
    public void shouldStealWhenFinishTurnInOwnEmptyPit() {
        var engine = prepareEngine();
        var game = prepareGame(engine, 4, 6, true, true);
        game.setStatus(GameStatus.ACTIVE);
        game.getBoard()[0] = 0;

        engine.turn(0, 3, game);

        assertArrayEquals(new int[]{0, 6, 6, 0, 9, 7, 7, 7, 0, 0}, game.getBoard());
        assertEquals(1, game.getCurrentPlayerIndex());
    }

    @Test
    public void shouldNotStealWhenItIsNotAllowed() {
        var engine = prepareEngine();
        var game = prepareGame(engine, 4, 6, false, true);
        game.setStatus(GameStatus.ACTIVE);
        game.getBoard()[0] = 0;

        engine.turn(0, 3, game);

        assertArrayEquals(new int[]{1, 6, 6, 0, 1, 7, 7, 7, 7, 0}, game.getBoard());
        assertEquals(1, game.getCurrentPlayerIndex());
    }

    @Test
    public void shouldNotStealWhenFinishTurnInOtherPlayerEmptyPit() {
        var engine = prepareEngine();
        var game = prepareGame(engine, 4, 6, true, true);
        game.setStatus(GameStatus.ACTIVE);
        game.getBoard()[6] = 0;

        engine.turn(0, 0, game);

        assertArrayEquals(new int[]{0, 7, 7, 7, 1, 7, 1, 6, 6, 0}, game.getBoard());
        assertEquals(1, game.getCurrentPlayerIndex());
    }

    @Test
    public void shouldFinishGameWhenOwnSpaceRowIsEmpty() {
        var engine = prepareEngine();
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
        var engine = prepareEngine();
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
        var engine = prepareEngine();
        var game = prepareGame(engine, 4, 4, true, true);
        game.setStatus(GameStatus.ACTIVE);

        engine.turn(0, 0, game);

        assertArrayEquals(new int[]{0, 5, 5, 5, 1, 4, 4, 4, 4, 0}, game.getBoard());
        assertEquals(0, game.getCurrentPlayerIndex());
        assertEquals(GameStatus.ACTIVE, game.getStatus());
    }

    @Test
    public void shouldNowHaveAnotherTurnWhenMultipleTurnIsNotAllowed() {
        var engine = prepareEngine();
        var game = prepareGame(engine, 4, 4, true, false);
        game.setStatus(GameStatus.ACTIVE);

        engine.turn(0, 0, game);

        assertArrayEquals(new int[]{0, 5, 5, 5, 1, 4, 4, 4, 4, 0}, game.getBoard());
        assertEquals(1, game.getCurrentPlayerIndex());
        assertEquals(GameStatus.ACTIVE, game.getStatus());
    }

}
