package com.bol.engine.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GameConfigurationTest {

    @Test
    public void shouldPrepareGame() {
        var pitsPerPlayer = 3;
        var stonesPerPit = 4;

        var game = prepareGame(pitsPerPlayer, stonesPerPit);
        var spaceRanges = game.getPlayerSpaces();
        assertEquals(spaceRanges.size(), 2);

        var firstSpaceRange = game.getPlayerSpaceRange(0);
        assertEquals(0, firstSpaceRange.firstPitIndex());
        assertEquals(2, firstSpaceRange.lastPitIndex());
        assertEquals(3, firstSpaceRange.storeIndex());

        var secondSpaceRange = game.getPlayerSpaceRange(1);
        assertEquals(4, secondSpaceRange.firstPitIndex());
        assertEquals(6, secondSpaceRange.lastPitIndex());
        assertEquals(7, secondSpaceRange.storeIndex());

        var board = game.getBoard();
        assertArrayEquals(new int[]{4, 4, 4, 0, 4, 4, 4, 0}, board);
    }

    @ParameterizedTest(name = "should fail when player index is {0}")
    @ValueSource(ints = {-1, 0})
    public void shouldValidatePitsPerPlayer(int pitsPerPlayer) {
        assertThrows(
                AssertionError.class,
                () -> prepareGame(pitsPerPlayer, 5)
        );
    }

    @ParameterizedTest(name = "should fail when player index is {0}")
    @ValueSource(ints = {-1, 0})
    public void shouldValidateStonesPerPit(int stonesPerPit) {
        assertThrows(
                AssertionError.class,
                () -> prepareGame(5, stonesPerPit)
        );
    }

    @Test
    public void shouldGetOtherPlayerSpaces() {
        var pitsPerPlayer = 3;
        var stonesPerPit = 4;

        var game = prepareGame(pitsPerPlayer, stonesPerPit);
        var spaceRanges = game.getOtherPlayerSpaces(0);
        assertEquals(spaceRanges.size(), 1);

        var spaceRange = spaceRanges.get(0);
        assertEquals(4, spaceRange.firstPitIndex());
        assertEquals(6, spaceRange.lastPitIndex());
        assertEquals(7, spaceRange.storeIndex());
    }

    @ParameterizedTest(name = "should fail when player index is {0}")
    @ValueSource(ints = {-1, 2})
    public void shouldValidatePlayerIndexWhenGetOtherPlayerSpaces(int playerIndex) {
        var game = prepareGame(5, 5);

        assertThrows(
                AssertionError.class,
                () -> game.getOtherPlayerSpaces(playerIndex)
        );
    }

    @ParameterizedTest(name = "should fail when player index is {0}")
    @ValueSource(ints = {-1, 2})
    public void shouldValidatePlayerIndexWhenSetWinnerIndex(int playerIndex) {
        var game = prepareGame(5, 5);

        assertThrows(
                AssertionError.class,
                () -> game.setWinnerIndex(playerIndex)
        );
    }

    @ParameterizedTest(name = "should fail when player index is {0}")
    @ValueSource(ints = {-1, 2})
    public void shouldValidatePlayerIndexWhenGetPlayerSpaceRange(int playerIndex) {
        var game = prepareGame(5, 5);

        assertThrows(
                AssertionError.class,
                () -> game.getPlayerSpaceRange(playerIndex)
        );
    }

    @Test
    public void shouldCalculateNextPlayer() {
        var game = prepareGame(5, 5);

        var currentPlayerIndex = game.getCurrentPlayerIndex();
        assertEquals(0, currentPlayerIndex);

        game.setNextPlayer();
        currentPlayerIndex = game.getCurrentPlayerIndex();
        assertEquals(1, currentPlayerIndex);

        game.setNextPlayer();
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
        var game = prepareGame(4, 5);
        var oppositeIndex = game.getOppositeSpaceIndex(index);
        assertEquals(expectedOppositeIndex, oppositeIndex);
    }

    @ParameterizedTest(name = "should fail when space index is {0}")
    @ValueSource(ints = {-1, 4, 9, 10})
    public void shouldValidateSpaceIndexWhenCalculateOppositeIndex(int spaceIndex) {
        var game = prepareGame(4, 5);

        assertThrows(
                AssertionError.class,
                () -> game.getOppositeSpaceIndex(spaceIndex)
        );
    }

    private static GameConfiguration prepareGame(int pitsPerPlayer, int stonesPerPit) {
        var game = new GameConfiguration(UUID.randomUUID(), pitsPerPlayer, stonesPerPit, true, true);
        game.addPlayer(UUID.randomUUID());
        game.initialize();

        return game;
    }
}