package com.bol.game.facade;

import com.bol.game.Game;
import com.bol.game.dto.request.CreateGameDto;
import com.bol.game.dto.request.RequestTurnDto;
import com.bol.game.dto.response.GameDto;
import com.bol.game.service.GameService;
import com.bol.message.dto.GameMessage;
import com.bol.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GameFacadeImpl implements GameFacade {

    private final GameService gameService;

    private final MessageService messageService;

    @Override
    public GameDto createGame(UUID userId, CreateGameDto body) {
        return toDto(gameService.createGame(userId, body));
    }

    @Override
    public GameDto joinGame(UUID userId, UUID gameId) {
        var game = gameService.joinGame(userId, gameId);
        var message = toMessage(game);
        messageService.sendGameStateUpdated(message);

        return toDto(game);
    }

    @Override
    public void requestTurn(UUID userId, UUID gameId, RequestTurnDto body) {
        var game = gameService.requestTurn(userId, gameId, body);
        var message = toMessage(game);

        messageService.sendGameStateUpdated(message);
    }

    private static GameDto toDto(Game game) {
        var state = game.getState();
        return new GameDto(game.getId(), state.getStatus());
    }

    private static GameMessage toMessage(Game game) {
        var state = game.getState();
        return new GameMessage(
                game.getId(), state.getPitsPerPlayer(), state.getStatus(), state.getBoard(),
                state.getPlayers(), state.getCurrentPlayerIndex(), state.getWinnerIndex()
        );
    }
}
