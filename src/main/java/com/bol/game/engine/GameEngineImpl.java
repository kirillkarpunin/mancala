package com.bol.game.engine;

import com.bol.game.engine.model.GameState;
import com.bol.game.engine.model.GameStatus;
import com.bol.game.engine.model.Player;
import com.bol.game.engine.model.SpaceRange;
import com.bol.game.engine.validation.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static com.bol.game.engine.util.GameEngineUtil.NUMBER_OF_PLAYERS;
import static com.bol.game.engine.util.GameEngineUtil.collectRemainingStones;
import static com.bol.game.engine.util.GameEngineUtil.determineWinner;
import static com.bol.game.engine.util.GameEngineUtil.isGameFinished;
import static com.bol.game.engine.util.GameEngineUtil.pickUpStones;
import static com.bol.game.engine.util.GameEngineUtil.setNextPlayer;
import static com.bol.game.engine.util.GameEngineUtil.shouldHaveAnotherTurn;
import static com.bol.game.engine.util.GameEngineUtil.sowStones;
import static com.bol.game.engine.util.GameEngineUtil.tryStealStones;

@Component
@RequiredArgsConstructor
public class GameEngineImpl implements GameEngine {

    private final RequestValidator requestValidator;

    @Override
    public GameState createGame(
            int pitsPerPlayer, int stonesPerPit, boolean isStealingAllowed, boolean isMultipleTurnAllowed
    ) {
        requestValidator.validateCreateGameRequest(pitsPerPlayer, stonesPerPit);

        var spacesPerPlayer = pitsPerPlayer + 1; // Number of spaces = number of pits + one store
        var board = new int[spacesPerPlayer * NUMBER_OF_PLAYERS];
        var players = new ArrayList<Player>();
        return new GameState(
                pitsPerPlayer, spacesPerPlayer, stonesPerPit, isStealingAllowed, isMultipleTurnAllowed, board, players
        );
    }

    @Override
    public void addPlayer(UUID userId, GameState game) {
        var players = game.getPlayers();
        var playerIndex = players.size();
        var firstPitIndex = playerIndex * game.getSpacesPerPlayer();
        var lastPitIndex = firstPitIndex + game.getPitsPerPlayer() - 1;
        var storeIndex = lastPitIndex + 1;
        var spaceRange = new SpaceRange(firstPitIndex, lastPitIndex, storeIndex);
        players.add(new Player(userId, spaceRange));
    }

    @Override
    public void turn(int playerIndex, int spaceIndex, GameState game) {
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
            setNextPlayer(game);
        }
    }

    @Override
    public void initialize(GameState game) {
        requestValidator.validateInitializeGameRequest(game, NUMBER_OF_PLAYERS);
        var players = game.getPlayers();
        var board = game.getBoard();
        var stonesPerPit = game.getStonesPerPit();

        players.forEach(player -> {
            var range = player.spaceRange();
            Arrays.fill(board, range.firstPitIndex(), range.storeIndex(), stonesPerPit);
        });

        game.setStatus(GameStatus.ACTIVE);
    }
}
