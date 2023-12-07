package com.bol.game.repository;

import com.bol.game.engine.model.GameConfiguration;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository {
    GameConfiguration save(GameConfiguration game);

    Optional<GameConfiguration> findById(UUID gameId);
}
