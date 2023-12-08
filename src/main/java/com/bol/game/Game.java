package com.bol.game;

import com.bol.game.engine.model.GameConfiguration;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // TODO: clean up
    @Type(JsonType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private GameConfiguration configuration;

    public Game() {
    }

    public Game(GameConfiguration configuration) {
        this.configuration = configuration;
    }

    public UUID getId() {
        return id;
    }

    public GameConfiguration getConfiguration() {
        return configuration;
    }
}