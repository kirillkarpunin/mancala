package com.bol.game.engine;

import com.bol.game.engine.model.GameConfiguration;
import com.bol.game.engine.model.GameStatus;
import com.bol.game.engine.model.SpaceRange;
import com.bol.game.engine.validation.RequestValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class GameEngineImpl implements GameEngine {

    private final RequestValidator requestValidator;

    public GameEngineImpl(RequestValidator requestValidator) {
        this.requestValidator = requestValidator;
    }

    @Override
    public GameConfiguration createGameConfiguration(
            UUID userId, int pitsPerPlayer, int stonesPerPit, boolean isStealingAllowed, boolean isMultipleTurnAllowed
    ) {
        requestValidator.validateCreateGameRequest(pitsPerPlayer, stonesPerPit);
        return new GameConfiguration(userId, pitsPerPlayer, stonesPerPit, isStealingAllowed, isMultipleTurnAllowed);
    }

    @Override
    public void turn(int playerIndex, int spaceIndex, GameConfiguration game) {
        requestValidator.validateTurnRequest(playerIndex, spaceIndex, game);

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

        var isWinnerDetermined = topScorePlayers.size() == 1;
        if (isWinnerDetermined) {
            return Optional.of(topScorePlayers.get(0));
        } else {
            return Optional.empty();
        }
    }

    private boolean shouldHaveAnotherTurn(int playerIndex, int lastInsertedPitIndex, GameConfiguration game) {
        if (!game.isMultipleTurnAllowed() || game.getStatus() != GameStatus.ACTIVE) {
            return false;
        }

        var storeIndex = game.getPlayerSpaceRange(playerIndex).storeIndex();
        return storeIndex == lastInsertedPitIndex;
    }
}
