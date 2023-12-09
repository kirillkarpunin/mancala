package com.bol.game.engine.util;

import com.bol.game.engine.model.GameState;
import com.bol.game.engine.model.GameStatus;
import com.bol.game.engine.model.Player;
import com.bol.game.engine.model.SpaceRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameEngineUtil {

    public static final int NUMBER_OF_PLAYERS = 2;

    public static int pickUpStones(int pitIndex, int[] board) {
        var stones = board[pitIndex];
        board[pitIndex] = 0;

        return stones;
    }

    public static int sowStones(int playerIndex, int stones, int currentSpaceIndex, GameState game) {
        var otherPlayerStoreIndexes = getOtherPlayerSpaces(game, playerIndex).stream()
                .map(SpaceRange::storeIndex)
                .collect(Collectors.toSet());
        var board = game.getBoard();

        while (stones > 0) {
            currentSpaceIndex = getNextSpaceIndex(currentSpaceIndex, game);
            if (!otherPlayerStoreIndexes.contains(currentSpaceIndex)) {
                board[currentSpaceIndex]++;
                stones--;
            }
        }

        return currentSpaceIndex;
    }

    public static void tryStealStones(int playerIndex, int lastInsertedPitIndex, GameState game) {
        if (!game.isStealingAllowed()) {
            return;
        }

        var spaceRange = getPlayerSpaceRange(game, playerIndex);

        var board = game.getBoard();
        var shouldSteal = lastInsertedPitIndex >= spaceRange.firstPitIndex()
                && lastInsertedPitIndex <= spaceRange.lastPitIndex()
                && board[lastInsertedPitIndex] == 1;
        if (shouldSteal) {
            var stones = pickUpStones(lastInsertedPitIndex, board);
            var oppositeSpaceIndex = getOppositeSpaceIndex(game, lastInsertedPitIndex);
            stones += pickUpStones(oppositeSpaceIndex, board);

            board[spaceRange.storeIndex()] += stones;
        }
    }

    public static boolean isGameFinished(GameState game) {
        var board = game.getBoard();
        var spaceRanges = getPlayerSpaces(game);
        return spaceRanges.stream().anyMatch(spaceRange -> isEmpty(spaceRange, board));
    }

    public static void collectRemainingStones(GameState game) {
        var spaceRanges = getPlayerSpaces(game);
        var board = game.getBoard();
        spaceRanges.forEach(spaceRange -> {
            var stones = pickUpStones(spaceRange.firstPitIndex(), spaceRange.lastPitIndex(), board);
            board[spaceRange.storeIndex()] += stones;
        });
    }

    public static Optional<Integer> determineWinner(GameState game) {
        var board = game.getBoard();
        var spaceRanges = getPlayerSpaces(game);

        var winnerScore = -1;
        var topScorePlayers = new ArrayList<Integer>();
        for (var i = 0; i < spaceRanges.size(); i++) {
            var stones = board[spaceRanges.get(i).storeIndex()];
            if (stones > winnerScore) {
                winnerScore = stones;
                topScorePlayers.clear();
                topScorePlayers.add(i);
            } else if (stones == winnerScore) {
                topScorePlayers.add(i);
            }
        }

        var isWinnerDetermined = topScorePlayers.size() == 1;
        if (isWinnerDetermined) {
            return Optional.of(topScorePlayers.get(0));
        } else {
            return Optional.empty();
        }
    }

    public static boolean shouldHaveAnotherTurn(int playerIndex, int lastInsertedPitIndex, GameState game) {
        if (!game.isMultipleTurnAllowed() || game.getStatus() != GameStatus.ACTIVE) {
            return false;
        }

        var storeIndex = getPlayerSpaceRange(game, playerIndex).storeIndex();
        return storeIndex == lastInsertedPitIndex;
    }

    public static void setNextPlayer(GameState game) {
        var currentPlayerIndex = game.getCurrentPlayerIndex();
        var nextPlayerIndex = (currentPlayerIndex + 1) % NUMBER_OF_PLAYERS;

        game.setCurrentPlayerIndex(nextPlayerIndex);
    }

    public static SpaceRange getPlayerSpaceRange(GameState game, int playerIndex) {
        validatePlayerIndex(playerIndex);

        return game.getPlayers().get(playerIndex).spaceRange();
    }

    private static int pickUpStones(int firstIndex, int lastIndex, int[] board) {
        var stones = 0;
        for (var i = firstIndex; i <= lastIndex; i++) {
            stones += pickUpStones(i, board);
        }
        return stones;
    }

    private static int getNextSpaceIndex(int pitIndex, GameState game) {
        return (pitIndex + 1) % game.getBoard().length;
    }

    private static boolean isEmpty(SpaceRange spaceRange, int[] board) {
        var first = spaceRange.firstPitIndex();
        var last = spaceRange.lastPitIndex();

        for (var i = first; i <= last; i++) {
            if (board[i] != 0) {
                return false;
            }
        }

        return true;
    }

    public static List<SpaceRange> getPlayerSpaces(GameState game) {
        return game.getPlayers().stream()
                .map(Player::spaceRange)
                .collect(Collectors.toList());
    }

    public static List<SpaceRange> getOtherPlayerSpaces(GameState game, int playerIndex) {
        validatePlayerIndex(playerIndex);

        var spaces = getPlayerSpaces(game);
        spaces.remove(playerIndex);
        return spaces;
    }

    public static int getOppositeSpaceIndex(GameState game, int spaceIndex) {
        assert spaceIndex >= 0 && spaceIndex < game.getBoard().length;

        var isStoreIndex = game.getPlayers().stream()
                .map(Player::spaceRange)
                .map(SpaceRange::storeIndex)
                .anyMatch(storeIndex -> storeIndex == spaceIndex);
        if (isStoreIndex) {
            throw new AssertionError("Can't calculate opposite index for store space");
        }

        return game.getBoard().length - NUMBER_OF_PLAYERS - spaceIndex;
    }

    private static void validatePlayerIndex(int playerIndex) {
        assert playerIndex >= 0 && playerIndex < NUMBER_OF_PLAYERS;
    }
}
