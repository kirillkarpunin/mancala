package com.bol.game.engine.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameConfiguration {
    private static final int NUMBER_OF_PLAYERS = 2;

    private final int pitsPerPlayer;
    private final int spacesPerPlayer;
    private final int stonesPerPit;
    private final int[] board;
    private final List<Player> players;
    private final boolean isStealingAllowed;
    private final boolean isMultipleTurnAllowed;

    private int currentPlayerIndex;
    private Integer winnerIndex;
    private GameStatus status;

    public GameConfiguration(
            UUID userId, int pitsPerPlayer, int stonesPerPit, boolean isStealingAllowed, boolean isMultipleTurnAllowed
    ) {
        assert pitsPerPlayer > 0;
        assert stonesPerPit > 0;

        this.pitsPerPlayer = pitsPerPlayer;
        this.spacesPerPlayer = pitsPerPlayer + 1; // Number of spaces = number of pits + one store
        this.stonesPerPit = stonesPerPit;
        this.players = new ArrayList<>();
        this.board = new int[spacesPerPlayer * NUMBER_OF_PLAYERS];
        this.isStealingAllowed = isStealingAllowed;
        this.isMultipleTurnAllowed = isMultipleTurnAllowed;

        this.currentPlayerIndex = 0;
        this.status = GameStatus.WAITING_FOR_PLAYERS;

        addPlayer(userId);
    }

    @JsonCreator
    public GameConfiguration(
            int pitsPerPlayer, int spacesPerPlayer, int stonesPerPit, int[] board, List<Player> players,
            boolean isStealingAllowed, boolean isMultipleTurnAllowed, int currentPlayerIndex, Integer winnerIndex,
            GameStatus status
    ) {
        this.pitsPerPlayer = pitsPerPlayer;
        this.spacesPerPlayer = spacesPerPlayer;
        this.stonesPerPit = stonesPerPit;
        this.board = board;
        this.players = players;
        this.isStealingAllowed = isStealingAllowed;
        this.isMultipleTurnAllowed = isMultipleTurnAllowed;
        this.currentPlayerIndex = currentPlayerIndex;
        this.winnerIndex = winnerIndex;
        this.status = status;
    }

    public void addPlayer(UUID userId) {
        var playerIndex = players.size();
        var firstPitIndex = playerIndex * spacesPerPlayer;
        var lastPitIndex = firstPitIndex + pitsPerPlayer - 1;
        var storeIndex = lastPitIndex + 1;
        var spaceRange = new SpaceRange(firstPitIndex, lastPitIndex, storeIndex);
        players.add(new Player(userId, spaceRange));
    }

    public void initialize() {
        assert players.size() == NUMBER_OF_PLAYERS;
        players.forEach(player -> {
            var range = player.spaceRange();
            Arrays.fill(board, range.firstPitIndex(), range.storeIndex(), stonesPerPit);
        });
        status = GameStatus.ACTIVE;
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

    @JsonIgnore
    public List<SpaceRange> getPlayerSpaces() {
        return players.stream()
                .map(Player::spaceRange)
                .collect(Collectors.toList());
    }

    public List<SpaceRange> getOtherPlayerSpaces(int playerIndex) {
        validatePlayerIndex(playerIndex);

        var spaces = getPlayerSpaces();
        spaces.remove(playerIndex);
        return spaces;
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
        validatePlayerIndex(winnerIndex);
        this.winnerIndex = winnerIndex;
    }

    public SpaceRange getPlayerSpaceRange(int playerIndex) {
        validatePlayerIndex(playerIndex);
        return players.get(playerIndex).spaceRange();
    }

    private static void validatePlayerIndex(int playerIndex) {
        assert playerIndex >= 0 && playerIndex < NUMBER_OF_PLAYERS;
    }

    public void setNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % NUMBER_OF_PLAYERS;
    }

    public int getOppositeSpaceIndex(int spaceIndex) {
        assert spaceIndex >= 0 && spaceIndex < board.length;
        var isStoreIndex = players.stream()
                .map(Player::spaceRange)
                .map(SpaceRange::storeIndex)
                .anyMatch(storeIndex -> storeIndex == spaceIndex);
        if (isStoreIndex) {
            throw new AssertionError("Can't calculate opposite index for store space");
        }

        return board.length - NUMBER_OF_PLAYERS - spaceIndex;
    }
}
