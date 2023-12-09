package com.bol.game.engine.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class GameConfiguration {

    private final int pitsPerPlayer;
    private final int spacesPerPlayer;
    private final int stonesPerPit;
    private final boolean isStealingAllowed;
    private final boolean isMultipleTurnAllowed;
    private final int[] board;
    private final List<Player> players;

    private int currentPlayerIndex;
    private Integer winnerIndex;
    private GameStatus status;

    public GameConfiguration(
            int pitsPerPlayer, int spacesPerPlayer, int stonesPerPit,
            boolean isStealingAllowed, boolean isMultipleTurnAllowed, int[] board
    ) {
        this(pitsPerPlayer, spacesPerPlayer, stonesPerPit, isStealingAllowed, isMultipleTurnAllowed, board,
                new ArrayList<>(), 0, GameStatus.WAITING_FOR_PLAYERS, null);
    }

    @JsonCreator
    public GameConfiguration(
            int pitsPerPlayer, int spacesPerPlayer, int stonesPerPit, boolean isStealingAllowed,
            boolean isMultipleTurnAllowed, int[] board, List<Player> players, int currentPlayerIndex,
            GameStatus status, Integer winnerIndex
    ) {
        this.pitsPerPlayer = pitsPerPlayer;
        this.spacesPerPlayer = spacesPerPlayer;
        this.stonesPerPit = stonesPerPit;
        this.board = board;
        this.players = players;
        this.isStealingAllowed = isStealingAllowed;
        this.isMultipleTurnAllowed = isMultipleTurnAllowed;
        this.currentPlayerIndex = currentPlayerIndex;
        this.status = status;
        this.winnerIndex = winnerIndex;
    }

    public int[] getBoard() {
        return board;
    }

    @JsonProperty("isStealingAllowed")
    public boolean isStealingAllowed() {
        return isStealingAllowed;
    }

    @JsonProperty("isMultipleTurnAllowed")
    public boolean isMultipleTurnAllowed() {
        return isMultipleTurnAllowed;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getPitsPerPlayer() {
        return pitsPerPlayer;
    }

    public int getSpacesPerPlayer() {
        return spacesPerPlayer;
    }

    public int getStonesPerPit() {
        return stonesPerPit;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Integer getWinnerIndex() {
        return winnerIndex;
    }

    public void setWinnerIndex(int winnerIndex) {
        this.winnerIndex = winnerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }
}
