package com.bol.game.engine.validation;

import com.bol.game.engine.exception.GameEngineException;
import com.bol.game.engine.model.GameConfiguration;
import com.bol.game.engine.model.GameStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.bol.game.engine.util.GameEngineUtil.getPlayerSpaceRange;

@Component
public class RequestValidatorImpl implements RequestValidator {

    private final Integer minPitsPerPlayer;
    private final Integer maxPitsPerPlayer;
    private final Integer minStonesPerPit;
    private final Integer maxStonesPerPit;

    public RequestValidatorImpl(
            @Value("${mancala.pitsPerPlayer.min}") Integer minPitsPerPlayer,
            @Value("${mancala.pitsPerPlayer.max}") Integer maxPitsPerPlayer,
            @Value("${mancala.stonesPerPit.min}") Integer minStonesPerPit,
            @Value("${mancala.stonesPerPit.max}") Integer maxStonesPerPit
    ) {
        this.minPitsPerPlayer = minPitsPerPlayer;
        this.maxPitsPerPlayer = maxPitsPerPlayer;
        this.minStonesPerPit = minStonesPerPit;
        this.maxStonesPerPit = maxStonesPerPit;
    }

    @Override
    public void validateCreateGameRequest(int pitsPerPlayer, int stonesPerPit) {
        if (pitsPerPlayer < minPitsPerPlayer || pitsPerPlayer > maxPitsPerPlayer) {
            var msg = "Invalid number of pits per player: allowedRange=[%d, %d], actualValue=%d"
                    .formatted(minPitsPerPlayer, maxPitsPerPlayer, pitsPerPlayer);
            throw new GameEngineException(msg);
        }

        if (stonesPerPit < minStonesPerPit || stonesPerPit > maxStonesPerPit) {
            var msg = "Invalid number of stones per pit: allowedRange=[%d, %d], actualValue=%d"
                    .formatted(minStonesPerPit, maxStonesPerPit, pitsPerPlayer);
            throw new GameEngineException(msg);
        }
    }

    @Override
    public void validateInitializeGameRequest(GameConfiguration game, int expectedNumberOfPlayers) {
        var numberOfPlayers = game.getPlayers().size();
        if (numberOfPlayers != expectedNumberOfPlayers) {
            var msg = "Invalid number of players: expected=%d, actual=%d"
                    .formatted(expectedNumberOfPlayers, numberOfPlayers);
            throw new GameEngineException(msg);
        }

        var gameStatus = game.getStatus();
        if (!gameStatus.equals(GameStatus.WAITING_FOR_PLAYERS)) {
            var msg = "Game is not in waiting state: gameStatus=%s".formatted(gameStatus);
            throw new GameEngineException(msg);
        }
    }

    @Override
    public void validateTurnRequest(int playerIndex, int spaceIndex, GameConfiguration game) {
        var gameStatus = game.getStatus();
        if (!gameStatus.equals(GameStatus.ACTIVE)) {
            var msg = "Game is not active: gameStatus=%s".formatted(gameStatus);
            throw new GameEngineException(msg);
        }

        var expectedPlayerIndex = game.getCurrentPlayerIndex();
        if (!(expectedPlayerIndex == playerIndex)) {
            var msg = "Turn request by the wrong player:expectedPlayer=%s, actualPlayer=%s"
                    .formatted(expectedPlayerIndex, playerIndex);
            throw new GameEngineException(msg);
        }

        var spaceRange = getPlayerSpaceRange(game, playerIndex);
        var firstPitIndex = spaceRange.firstPitIndex();
        var lastPitIndex = spaceRange.lastPitIndex();
        if (spaceIndex < firstPitIndex || spaceIndex > lastPitIndex) {
            var msg = "Selected space index is now allowed: selectedPitIndex=%d, allowedIndexRange=[%d,%d]"
                    .formatted(spaceIndex, firstPitIndex, lastPitIndex);
            throw new GameEngineException(msg);
        }

        var stonesInSelectedPit = game.getBoard()[spaceIndex];
        if (stonesInSelectedPit == 0) {
            var msg = "Selected pit is empty: selectedPitIndex=%d".formatted(spaceIndex);
            throw new GameEngineException(msg);
        }
    }
}
