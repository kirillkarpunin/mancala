package com.bol.game.engine.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor(onConstructor_ = {@JsonCreator})
@RequiredArgsConstructor
public class GameState {

    private final int pitsPerPlayer;
    private final int spacesPerPlayer;
    private final int stonesPerPit;

    @Getter(onMethod_ = {@JsonProperty("isStealingAllowed")})
    private final boolean isStealingAllowed;
    @Getter(onMethod_ = {@JsonProperty("isMultipleTurnAllowed")})
    private final boolean isMultipleTurnAllowed;

    private final int[] board;
    private final List<Player> players;

    @Setter
    private int currentPlayerIndex = 0;
    @Setter
    private Integer winnerIndex = null;
    @Setter
    private GameStatus status = GameStatus.WAITING_FOR_PLAYERS;
}
