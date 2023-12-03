package com.bol.service;

import com.bol.dto.request.CreateGameDto;
import com.bol.engine.GameEngine;
import com.bol.engine.model.GameConfiguration;
import com.bol.repository.GameRepository;
import org.springframework.stereotype.Service;

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
                body.pitsPerPlayer(),
                body.stonesPerSpace(),
                body.isStealingAllowed(),
                body.isMultipleTurnAllowed()
        );

        return gameRepository.save(game);
    }
}
