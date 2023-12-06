package com.bol.service;

import com.bol.auth.service.MessageService;
import com.bol.dto.request.CreateGameDto;
import com.bol.dto.request.RequestTurnDto;
import com.bol.dto.response.GameDto;
import com.bol.engine.GameEngine;
import com.bol.engine.model.GameConfiguration;
import com.bol.engine.model.GameStatus;
import com.bol.engine.model.Player;
import com.bol.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {
    private final GameEngine gameEngine;
    private final GameRepository gameRepository;

    private final MessageService messageService;

    public GameService(GameEngine gameEngine, GameRepository gameRepository, MessageService messageService) {
        this.gameEngine = gameEngine;
        this.gameRepository = gameRepository;
        this.messageService = messageService;
    }

    public GameDto createGame(UUID userId, CreateGameDto body) {
        var game = gameEngine.createGame(
                userId,
                body.pitsPerPlayer(),
                body.stonesPerSpace(),
                body.isStealingAllowed(),
                body.isMultipleTurnAllowed()
        );

        return toDto(gameRepository.save(game));
    }

    public GameDto joinGame(UUID userId, UUID gameId) {
        // TODO: Acquire lock
        var game = findGameById(gameId);
        var status = game.getStatus();
        if (status != GameStatus.WAITING_FOR_PLAYERS) {
            throw new IllegalStateException("Game is not in waiting state: gameId=%s, gameStatus=%s".formatted(gameId, status));
        }

        var isAlreadyJoined = game.getPlayers().stream()
                .map(Player::userId)
                .anyMatch(userId::equals);
        if (isAlreadyJoined) {
            throw new IllegalStateException("User is already joined: gameId=%s, userId=%s".formatted(gameId, userId));
        }

        game.addPlayer(userId);
        game.initialize();

        var result = toDto(gameRepository.save(game));
        messageService.sendState(result);

        return result;
    }

    private GameConfiguration findGameById(UUID gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalStateException("Game is not found: gameId=%s".formatted(gameId)));
    }

    public GameDto requestTurn(UUID gameId, RequestTurnDto message) {
        // TODO: Acquire lock
        var game = findGameById(gameId);
        var status = game.getStatus();
        if (status != GameStatus.ACTIVE) {
            throw new IllegalStateException("Game is not in active state: gameId=%s, gameStatus=%s".formatted(gameId, status));
        }

        var userId = message.userId();
        var playerIndex = getPlayerIndex(userId, game)
                .orElseThrow(() -> new IllegalStateException("User is not a player: gameId=%s, userId=%s".formatted(gameId, userId)));

        gameEngine.turn(playerIndex, message.spaceIndex(), game);


        // TODO: Refactor
        var result = toDto(gameRepository.save(game));
        messageService.sendState(result);

        return result;
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

    private static GameDto toDto(GameConfiguration gameConfiguration) {
        return new GameDto(gameConfiguration.getId(), gameConfiguration.getStatus());
    }
}
