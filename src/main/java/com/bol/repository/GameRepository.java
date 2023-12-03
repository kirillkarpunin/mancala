package com.bol.repository;

import com.bol.engine.model.GameConfiguration;

public interface GameRepository {
    GameConfiguration save(GameConfiguration game);
}
