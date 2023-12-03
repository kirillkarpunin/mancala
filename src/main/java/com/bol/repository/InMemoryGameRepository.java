package com.bol.repository;

import com.bol.engine.model.GameConfiguration;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

@Repository
public class InMemoryGameRepository implements GameRepository {

    // TODO: Concurrency support
    private final Map<UUID, GameConfiguration> games = new HashMap<>();

    @Override
    public GameConfiguration save(GameConfiguration game) {
        games.put(game.getId(), game);
        return game;
    }
}
