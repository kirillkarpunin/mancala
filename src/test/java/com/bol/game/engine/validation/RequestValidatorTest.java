package com.bol.game.engine.validation;

import com.bol.game.engine.exception.GameEngineException;
import com.bol.game.engine.model.GameConfiguration;
import com.bol.game.engine.model.GameStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestValidatorTest {

    private static final int DEFAULT_PITS_PER_PLAYER = 6;
    private static final int DEFAULT_STONES_PER_PIT = 4;


    @ParameterizedTest(name = "should fail when create game with pits per player = {0}")
    @ValueSource(ints = {-1, 0, 3, 4})
    public void shouldFailWhenCreateGameWithInvalidPitsPerPlayer(int pitsPerPlayer) {
        var minPitsPerPlayer = 1;
        var maxPitsPerPlayer = 2;
        var validator = prepareRequestValidator(
                minPitsPerPlayer, maxPitsPerPlayer, DEFAULT_STONES_PER_PIT, DEFAULT_STONES_PER_PIT
        );

        var exception = assertThrows(
                GameEngineException.class,
                () -> validator.validateCreateGameRequest(pitsPerPlayer, DEFAULT_STONES_PER_PIT)
        );

        assertTrue(exception.getMessage().contains("Invalid number of pits per player"));
    }

    @ParameterizedTest(name = "should fail when create game with stones per pit = {0}")
    @ValueSource(ints = {-1, 0, 3, 4})
    public void shouldFailWhenCreateGameWithInvalidStonesPerPit(int stonesPerPit) {
        var minStonesPerPit = 1;
        var maxStonesPerPit = 2;
        var validator = prepareRequestValidator(
                DEFAULT_PITS_PER_PLAYER, DEFAULT_PITS_PER_PLAYER, minStonesPerPit, maxStonesPerPit
        );

        var exception = assertThrows(
                GameEngineException.class,
                () -> validator.validateCreateGameRequest(DEFAULT_PITS_PER_PLAYER, stonesPerPit)
        );

        assertTrue(exception.getMessage().contains("Invalid number of stones per pit"));
    }

    @ParameterizedTest(name = "should fail when request turn for {0} game")
    @EnumSource(value = GameStatus.class, names = "ACTIVE", mode = EnumSource.Mode.EXCLUDE)
    public void shouldFailWhenRequestTurnForNotActiveGame(GameStatus status) {
        var validator = prepareRequestValidator();
        var game = prepareGame(5, 5, true, true);

        game.setStatus(status);
        var exception = assertThrows(
                GameEngineException.class,
                () -> validator.validateTurnRequest(0, 0, game)
        );
        assertTrue(exception.getMessage().contains("Game is not active"));
    }


    @ParameterizedTest(name = "should fail when request turn by player # {0}")
    @ValueSource(ints = {-1, 1, 2})
    public void shouldFailWhenRequestTurnByWrongPlayer(int playerIndex) {
        var validator = prepareRequestValidator();
        var game = prepareGame(5, 5, true, true);
        game.setStatus(GameStatus.ACTIVE);
        var exception = assertThrows(
                GameEngineException.class,
                () -> validator.validateTurnRequest(playerIndex, 0, game)
        );

        assertTrue(exception.getMessage().contains("Turn request by the wrong player"));
    }

    @ParameterizedTest(name = "should fail when request turn with space index {0}")
    @ValueSource(ints = {-1, 3, 4, 5, 6, 7, 8})
    public void shouldFailWhenRequestTurnWithInvalidSpaceIndex(int spaceIndex) {
        var validator = prepareRequestValidator();
        var game = prepareGame(3, 5, true, true);
        game.setStatus(GameStatus.ACTIVE);
        var exception = assertThrows(
                GameEngineException.class,
                () -> validator.validateTurnRequest(0, spaceIndex, game)
        );

        assertTrue(exception.getMessage().contains("Selected space index is now allowed"));
    }

    @Test
    public void shouldFailWhenRequestTurnWithEmptyPitSpace() {
        var validator = prepareRequestValidator();
        var game = prepareGame(3, 5, true, true);
        game.setStatus(GameStatus.ACTIVE);

        var spaceIndex = 0;
        game.getBoard()[spaceIndex] = 0;

        var exception = assertThrows(
                GameEngineException.class,
                () -> validator.validateTurnRequest(0, spaceIndex, game)
        );

        assertTrue(exception.getMessage().contains("Selected pit is empty"));
    }

    private static GameConfiguration prepareGame(int pitsPerPlayer, int stonesPerPit, boolean isStealingAllowed, boolean isMultipleTurnAllowed) {
        var game = new GameConfiguration(UUID.randomUUID(), pitsPerPlayer, stonesPerPit, isStealingAllowed, isMultipleTurnAllowed);
        game.addPlayer(UUID.randomUUID());
        game.initialize();

        return game;
    }

    private static RequestValidatorImpl prepareRequestValidator(
            Integer minPitsPerPlayer, Integer maxPitsPerPlayer, Integer minStonesPerPit, Integer maxStonesPerPit
    ) {
        return new RequestValidatorImpl(minPitsPerPlayer, maxPitsPerPlayer, minStonesPerPit, maxStonesPerPit);
    }

    private static RequestValidatorImpl prepareRequestValidator() {
        return prepareRequestValidator(
                DEFAULT_PITS_PER_PLAYER, DEFAULT_PITS_PER_PLAYER, DEFAULT_STONES_PER_PIT, DEFAULT_STONES_PER_PIT
        );
    }
}