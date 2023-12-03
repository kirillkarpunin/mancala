package com.bol.engine;

import com.bol.engine.exception.GameEngineException;
import com.bol.engine.model.GameStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    @ParameterizedTest(name = "should fail when request turn for {0} game")
    @EnumSource(value = GameStatus.class, names = "ACTIVE", mode = EnumSource.Mode.EXCLUDE)
    void shouldFailWhenRequestTurnForNotActiveGame(GameStatus status) {
        var engine = new GameEngine();
        var game = engine.createGame(5, 5, true, true);

        game.setStatus(status);
        assertThrows(
                GameEngineException.class,
                () -> engine.turn(0, 0, game)
        );
    }

    @ParameterizedTest(name = "should fail when request turn by player # {0}")
    @ValueSource(ints = {-1, 1, 2})
    void shouldFailWhenRequestTurnByWrongPlayer(int playerIndex) {
        var engine = new GameEngine();
        var game = engine.createGame(5, 5, true, true);
        game.setStatus(GameStatus.ACTIVE);
        assertThrows(
                GameEngineException.class,
                () -> engine.turn(playerIndex, 0, game)
        );
    }

    @ParameterizedTest(name = "should fail when request turn with space index {0}")
    @ValueSource(ints = {-1, 3, 4, 5, 6, 7, 8})
    void shouldFailWhenRequestTurnWithInvalidSpaceIndex(int spaceIndex) {
        var engine = new GameEngine();
        var game = engine.createGame(3, 5, true, true);
        game.setStatus(GameStatus.ACTIVE);
        assertThrows(
                GameEngineException.class,
                () -> engine.turn(0, spaceIndex, game)
        );
    }

    @Test
    void shouldFailWhenRequestTurnWithEmptyPitSpace() {
        var engine = new GameEngine();
        var game = engine.createGame(3, 5, true, true);
        game.setStatus(GameStatus.ACTIVE);

        var spaceIndex = 0;
        game.getBoard()[spaceIndex] = 0;

        assertThrows(
                GameEngineException.class,
                () -> engine.turn(0, spaceIndex, game)
        );
    }
}