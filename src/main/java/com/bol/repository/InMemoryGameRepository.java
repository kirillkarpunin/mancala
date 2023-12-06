package com.bol.repository;

import com.bol.engine.model.GameConfiguration;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository implements GameRepository {

    // TODO: Concurrency support
    private final Map<UUID, GameConfiguration> games = new ConcurrentHashMap<>();

    @Override
    public GameConfiguration save(GameConfiguration game) {
        games.put(game.getId(), game);
        return game;
    }

    @Override
    public Optional<GameConfiguration> findById(UUID gameId) {
        return Optional.of(games.get(gameId));
    }
}
