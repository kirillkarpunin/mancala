package com.bol.service;

import com.bol.dto.request.CreateGameDto;
import com.bol.dto.request.JoinGameDto;
import com.bol.engine.GameEngine;
import com.bol.engine.model.GameConfiguration;
import com.bol.engine.model.GameStatus;
import com.bol.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GameService {
    private final GameEngine gameEngine;
    private final GameRepository gameRepository;

    public GameService(GameEngine gameEngine, GameRepository gameRepository) {
        this.gameEngine = gameEngine;
        this.gameRepository = gameRepository;
    }

    public GameConfiguration createGame(CreateGameDto body) {
        var game = gameEngine.createGame(
                body.userId(),
                body.pitsPerPlayer(),
                body.stonesPerSpace(),
                body.isStealingAllowed(),
                body.isMultipleTurnAllowed()
        );

        return gameRepository.save(game);
    }

    public GameConfiguration joinGame(UUID gameId, JoinGameDto body) {
        // TODO: Acquire lock
        var game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalStateException("Game is not found: gameId=%s".formatted(gameId)));
        var status = game.getStatus();
        if(status != GameStatus.WAITING_FOR_PLAYERS) {
           throw new IllegalStateException("Game is not in waiting state: gameId=%s, gameStatus=%s".formatted(gameId, status));
        }
        game.addPlayer(body.userId());
        game.initialize();
        gameRepository.save(game);

        return game;
    }
}
