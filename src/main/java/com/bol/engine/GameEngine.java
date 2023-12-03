package com.bol.engine;

import com.bol.engine.exception.GameEngineException;
import com.bol.engine.model.GameConfiguration;
import com.bol.engine.model.GameStatus;
import com.bol.engine.model.SpaceRange;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: Consider interface
@Component
public class GameEngine {

    public GameConfiguration createGame(
            int pitsPerPlayer, int stonesPerPit, boolean isStealingAllowed, boolean isMultipleTurnAllowed
    ) {
        return new GameConfiguration(pitsPerPlayer, stonesPerPit, isStealingAllowed, isMultipleTurnAllowed);
    }

    public void turn(int playerIndex, int spaceIndex, GameConfiguration game) {
        // TODO: Acquire lock
        validateTurnRequest(playerIndex, spaceIndex, game);

        var stones = pickUpStones(spaceIndex, game.getBoard());
        var lastInsertedPitIndex = sowStones(playerIndex, stones, spaceIndex, game);

        tryStealStones(playerIndex, lastInsertedPitIndex, game);

        if (isGameFinished(game)) {
            collectRemainingStones(game);
            determineWinner(game).ifPresent(game::setWinnerIndex);
            game.setStatus(GameStatus.FINISHED);
            return;
        }

        if (!shouldHaveAnotherTurn(playerIndex, lastInsertedPitIndex, game)) {
            game.setNextPlayer();
        }
    }

    private int pickUpStones(int firstIndex, int lastIndex, int[] board) {
        var stones = 0;
        for (var i = firstIndex; i <= lastIndex; i++) {
            stones += pickUpStones(i, board);
        }
        return stones;
    }

    private static void validateTurnRequest(int playerIndex, int spaceIndex, GameConfiguration game) {
        var gameId = game.getId();
        var gameStatus = game.getStatus();
        if (!gameStatus.equals(GameStatus.ACTIVE)) {
            var msg = "Game is not active: gameId=%s, gameStatus=%s".formatted(gameId, gameStatus);
            throw new GameEngineException(msg);
        }

        var expectedPlayerIndex = game.getCurrentPlayerIndex();
        if (!(expectedPlayerIndex == playerIndex)) {
            var msg = "Turn request by the wrong player: gameId=%s, expectedPlayer=%s, actualPlayer=%s"
                    .formatted(gameId, expectedPlayerIndex, playerIndex);
            throw new GameEngineException(msg);
        }

        var spaceRange = game.getPlayerSpaceRange(playerIndex);
        var firstPitIndex = spaceRange.firstPitIndex();
        var lastPitIndex = spaceRange.lastPitIndex();
        if (spaceIndex < firstPitIndex || spaceIndex > lastPitIndex) {
            var msg = "Selected space index is now allowed: gameId=%s, selectedPitIndex=%d, allowedIndexRange=[%d,%d]"
                    .formatted(gameId, spaceIndex, firstPitIndex, lastPitIndex);
            throw new GameEngineException(msg);
        }

        var stonesInSelectedPit = game.getBoard()[spaceIndex];
        if (stonesInSelectedPit == 0) {
            var msg = "Selected pit is empty: gameId=%s, selectedPitIndex=%d".formatted(gameId, spaceIndex);
            throw new GameEngineException(msg);
        }
    }

    private static int pickUpStones(int pitIndex, int[] board) {
        var stones = board[pitIndex];
        board[pitIndex] = 0;

        return stones;
    }

    private static int sowStones(int playerIndex, int stones, int currentSpaceIndex, GameConfiguration game) {
        var otherPlayerStoreIndexes = game.getOtherPlayerSpaces(playerIndex).stream()
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

    public static int getNextSpaceIndex(int pitIndex, GameConfiguration game) {
        return (pitIndex + 1) % game.getBoard().length;
    }

    private void tryStealStones(int playerIndex, int lastInsertedPitIndex, GameConfiguration game) {
        if (!game.isStealingAllowed()) {
            return;
        }

        var spaceRange = game.getPlayerSpaceRange(playerIndex);

        var board = game.getBoard();
        var shouldSteal = lastInsertedPitIndex >= spaceRange.firstPitIndex()
                && lastInsertedPitIndex <= spaceRange.lastPitIndex()
                && board[lastInsertedPitIndex] == 1;
        if (shouldSteal) {
            var stones = pickUpStones(lastInsertedPitIndex, board);
            var oppositeSpaceIndex = game.getOppositeSpaceIndex(lastInsertedPitIndex);
            stones += pickUpStones(oppositeSpaceIndex, board);

            board[spaceRange.storeIndex()] += stones;
        }
    }

    private boolean isGameFinished(GameConfiguration game) {
        var board = game.getBoard();
        var spaceRanges = game.getPlayerSpaces();
        return spaceRanges.stream().anyMatch(spaceRange -> isEmpty(spaceRange, board));
    }

    private boolean isEmpty(SpaceRange spaceRange, int[] board) {
        var first = spaceRange.firstPitIndex();
        var last = spaceRange.lastPitIndex();

        for (var i = first; i <= last; i++) {
            if (board[i] != 0) {
                return false;
            }
        }

        return true;
    }

    private void collectRemainingStones(GameConfiguration game) {
        var spaceRanges = game.getPlayerSpaces();
        var board = game.getBoard();
        spaceRanges.forEach(spaceRange -> {
            var stones = pickUpStones(spaceRange.firstPitIndex(), spaceRange.lastPitIndex(), board);
            board[spaceRange.storeIndex()] += stones;
        });
    }

    private Optional<Integer> determineWinner(GameConfiguration game) {
        var board = game.getBoard();
        var spaceRanges = game.getPlayerSpaces();

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

        return topScorePlayers.size() == 1 ? Optional.of(topScorePlayers.get(0)) : Optional.empty();
    }

    private boolean shouldHaveAnotherTurn(int playerIndex, int lastInsertedPitIndex, GameConfiguration game) {
        if (!game.isMultipleTurnAllowed() || game.getStatus() != GameStatus.ACTIVE) {
            return false;
        }

        var storeIndex = game.getPlayerSpaceRange(playerIndex).storeIndex();
        return storeIndex == lastInsertedPitIndex;
    }
}
