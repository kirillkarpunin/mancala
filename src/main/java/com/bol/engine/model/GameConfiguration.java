package com.bol.engine.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GameConfiguration {
    private static final int NUMBER_OF_PLAYERS = 2;

    private final UUID id;
    private final int spacesPerPlayer;
    private final int[] board;
    private final List<SpaceRange> playerSpaces;
    private final boolean isStealingAllowed;
    private final boolean isMultipleTurnAllowed;

    private int currentPlayerIndex;
    private Integer winnerIndex;
    private GameStatus status;

    public GameConfiguration(int pitsPerPlayer, int stonesPerPit, boolean isStealingAllowed, boolean isMultipleTurnAllowed) {
        this.id = UUID.randomUUID();
        this.spacesPerPlayer = pitsPerPlayer + 1; // Number of spaces = number of pits + one store
        this.playerSpaces = preparePlayerSpaces(pitsPerPlayer);
        this.board = prepareBoard(stonesPerPit);
        this.isStealingAllowed = isStealingAllowed;
        this.isMultipleTurnAllowed = isMultipleTurnAllowed;

        this.currentPlayerIndex = 0;
        this.status = GameStatus.WAITING_FOR_PLAYERS;
    }

    private List<SpaceRange> preparePlayerSpaces(int pitsPerPlayer) {
        var playerSpaces = new ArrayList<SpaceRange>();

        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            var firstPitIndex = i * spacesPerPlayer;
            var lastPitIndex = firstPitIndex + pitsPerPlayer;
            var storeIndex = lastPitIndex + 1;
            var spaceRange = new SpaceRange(firstPitIndex, lastPitIndex, storeIndex);
            playerSpaces.add(spaceRange);
        }

        return playerSpaces;
    }

    private int[] prepareBoard(int stonesPerPit) {
        var board = new int[spacesPerPlayer * NUMBER_OF_PLAYERS];
        playerSpaces.forEach(
                range -> Arrays.fill(board, range.firstPitIndex(), range.lastPitIndex(), stonesPerPit));

        return board;
    }

    public UUID getId() {
        return id;
    }

    public int[] getBoard() {
        return board;
    }


    public boolean isStealingAllowed() {
        return isStealingAllowed;
    }

    public boolean isMultipleTurnAllowed() {
        return isMultipleTurnAllowed;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public List<SpaceRange> getPlayerSpaces() {
        return playerSpaces;
    }

    public List<SpaceRange> getOtherPlayerSpaces(int playerIndex) {
        validatePlayerIndex(playerIndex);

        var spaces = new ArrayList<>(playerSpaces);
        spaces.remove(playerIndex);
        return spaces;
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
        return playerSpaces.get(playerIndex);
    }

    private static void validatePlayerIndex(int playerIndex) {
        assert playerIndex >= 0 && playerIndex < NUMBER_OF_PLAYERS;
    }

    public void setNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % NUMBER_OF_PLAYERS;
    }

    public int getOppositeSpaceIndex(int spaceIndex) {
        assert spaceIndex >= 0 && spaceIndex < board.length;
        return board.length - NUMBER_OF_PLAYERS - spaceIndex;
    }
}
