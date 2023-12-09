package com.bol.game.engine.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static com.bol.TestUtil.prepareGame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameEngineUtilTest {

    @Test
    public void shouldGetOtherPlayerSpaces() {
        var pitsPerPlayer = 3;
        var stonesPerPit = 4;

        var game = prepareGame(pitsPerPlayer, stonesPerPit, true, true);
        var spaceRanges = GameEngineUtil.getOtherPlayerSpaces(game, 0);
        assertEquals(spaceRanges.size(), 1);

        var spaceRange = spaceRanges.get(0);
        assertEquals(4, spaceRange.firstPitIndex());
        assertEquals(6, spaceRange.lastPitIndex());
        assertEquals(7, spaceRange.storeIndex());
    }

    @ParameterizedTest(name = "should fail when player index is {0}")
    @ValueSource(ints = {-1, 2})
    public void shouldValidatePlayerIndexWhenGetOtherPlayerSpaces(int playerIndex) {
        var game = prepareGame(5, 5, true, true);

        assertThrows(
                AssertionError.class,
                () -> GameEngineUtil.getOtherPlayerSpaces(game, playerIndex)
        );
    }

    @ParameterizedTest(name = "should fail when player index is {0}")
    @ValueSource(ints = {-1, 2})
    public void shouldValidatePlayerIndexWhenGetPlayerSpaceRange(int playerIndex) {
        var game = prepareGame(5, 5, true, true);

        assertThrows(
                AssertionError.class,
                () -> GameEngineUtil.getPlayerSpaceRange(game, playerIndex)
        );
    }

    @Test
    public void shouldCalculateNextPlayer() {
        var game = prepareGame(5, 5, true, true);

        var currentPlayerIndex = game.getCurrentPlayerIndex();
        assertEquals(0, currentPlayerIndex);

        GameEngineUtil.setNextPlayer(game);
        currentPlayerIndex = game.getCurrentPlayerIndex();
        assertEquals(1, currentPlayerIndex);

        GameEngineUtil.setNextPlayer(game);
        currentPlayerIndex = game.getCurrentPlayerIndex();
        assertEquals(0, currentPlayerIndex);
    }

    private static Stream<Arguments> shouldCalculateOppositeIndexArguments() {
        return Stream.of(
                Arguments.of(0, 8),
                Arguments.of(1, 7),
                Arguments.of(2, 6),
                Arguments.of(3, 5),
                Arguments.of(5, 3),
                Arguments.of(6, 2),
                Arguments.of(7, 1),
                Arguments.of(8, 0)
        );
    }

    @ParameterizedTest(name = "opposite index for {0} should be {1}")
    @MethodSource("shouldCalculateOppositeIndexArguments")
    public void shouldCalculateOppositeIndex(int index, int expectedOppositeIndex) {
        var game = prepareGame(4, 5, true, true);
        var oppositeIndex = GameEngineUtil.getOppositeSpaceIndex(game, index);
        assertEquals(expectedOppositeIndex, oppositeIndex);
    }

    @ParameterizedTest(name = "should fail when space index is {0}")
    @ValueSource(ints = {-1, 4, 9, 10})
    public void shouldValidateSpaceIndexWhenCalculateOppositeIndex(int spaceIndex) {
        var game = prepareGame(4, 5, true, true);

        assertThrows(
                AssertionError.class,
                () -> GameEngineUtil.getOppositeSpaceIndex(game, spaceIndex)
        );
    }
}