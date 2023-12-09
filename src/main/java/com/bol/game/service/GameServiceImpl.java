package com.bol.game.service;

import com.bol.exception.ApplicationException;
import com.bol.game.Game;
import com.bol.game.dto.request.CreateGameDto;
import com.bol.game.dto.request.RequestTurnDto;
import com.bol.game.engine.GameEngine;
import com.bol.game.engine.exception.GameEngineException;
import com.bol.game.engine.model.GameConfiguration;
import com.bol.game.engine.model.GameStatus;
import com.bol.game.engine.model.Player;
import com.bol.game.repository.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {
    private final GameEngine gameEngine;
    private final GameRepository gameRepository;

    public GameServiceImpl(GameEngine gameEngine, GameRepository gameRepository) {
        this.gameEngine = gameEngine;
        this.gameRepository = gameRepository;
    }

    @Override
    @Transactional
    public Game createGame(UUID userId, CreateGameDto body) {
        var configuration = gameEngine.createGameConfiguration(
                body.pitsPerPlayer(),
                body.stonesPerSpace(),
                body.isStealingAllowed(),
                body.isMultipleTurnAllowed()
        );
        gameEngine.addPlayer(userId, configuration);

        return gameRepository.save(new Game(configuration));
    }

    @Override
    @Transactional
    public Game joinGame(UUID userId, UUID gameId) {
        var game = lockGameById(gameId);
        var configuration = game.getConfiguration();
        var status = configuration.getStatus();
        if (status != GameStatus.WAITING_FOR_PLAYERS) {
            throw ApplicationException.badRequest("Game is not in waiting state: gameId=%s, gameStatus=%s".formatted(gameId, status));
        }

        var isAlreadyJoined = configuration.getPlayers().stream()
                .map(Player::userId)
                .anyMatch(userId::equals);
        if (isAlreadyJoined) {
            throw ApplicationException.badRequest("User is already joined: gameId=%s, userId=%s".formatted(gameId, userId));
        }

        gameEngine.addPlayer(userId, configuration);
        gameEngine.initialize(configuration);

        return gameRepository.save(game);
    }

    private Game lockGameById(UUID gameId) {
        return gameRepository.lockById(gameId)
                .orElseThrow(() -> ApplicationException.badRequest("Game is not found: gameId=%s".formatted(gameId)));
    }

    @Override
    @Transactional
    public Game requestTurn(UUID gameId, RequestTurnDto body) {
        var game = lockGameById(gameId);
        var configuration = game.getConfiguration();
        var status = configuration.getStatus();
        if (status != GameStatus.ACTIVE) {
            throw ApplicationException.badRequest("Game is not in active state: gameId=%s, gameStatus=%s".formatted(gameId, status));
        }

        var userId = body.userId();
        var playerIndex = getPlayerIndex(userId, configuration)
                .orElseThrow(() -> ApplicationException.badRequest("User is not a player: gameId=%s, userId=%s".formatted(gameId, userId)));

        wrapGameEngineException(
                gameId, () -> gameEngine.turn(playerIndex, body.spaceIndex(), configuration)
        );

        return gameRepository.save(game);
    }

    private static Optional<Integer> getPlayerIndex(UUID userId, GameConfiguration game) {
        // TODO: Refactor, consider moving playerIndex to Player structure
        var players = game.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            var player = players.get(i);
            if (player.userId().equals(userId)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    private void wrapGameEngineException(UUID gameId, Runnable function) {
        try {
            function.run();
        } catch (GameEngineException exception) {
            var msg = "Request turn for gameId=%s failed. Engine message: %s"
                    .formatted(gameId, exception.getMessage());
            throw ApplicationException.badRequest(msg);
        }
    }
}
