package com.bol.game.facade;

import com.bol.game.Game;
import com.bol.game.dto.request.CreateGameDto;
import com.bol.game.dto.request.RequestTurnDto;
import com.bol.game.dto.response.GameDto;
import com.bol.game.service.GameService;
import com.bol.message.dto.GameMessage;
import com.bol.message.service.MessageService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GameFacadeImpl implements GameFacade {

    private final GameService gameService;

    private final MessageService messageService;

    public GameFacadeImpl(GameService gameService, MessageService messageService) {
        this.gameService = gameService;
        this.messageService = messageService;
    }

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
    public void requestTurn(UUID gameId, RequestTurnDto body) {
        var game = gameService.requestTurn(gameId, body);
        var message = toMessage(game);

        messageService.sendGameStateUpdated(message);
    }

    private static GameDto toDto(Game game) {
        var configuration = game.getConfiguration();
        return new GameDto(game.getId(), configuration.getStatus());
    }

    private static GameMessage toMessage(Game game) {
        var configuration = game.getConfiguration();
        return new GameMessage(game.getId(), configuration.getStatus());
    }
}
