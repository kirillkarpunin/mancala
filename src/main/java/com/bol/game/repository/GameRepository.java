package com.bol.game.repository;

import com.bol.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
    Game save(Game game);

    Optional<Game> findById(UUID id);
}
