package com.bol.repository;

import com.bol.engine.model.GameConfiguration;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository {
    GameConfiguration save(GameConfiguration game);

    Optional<GameConfiguration> findById(UUID gameId);
}
