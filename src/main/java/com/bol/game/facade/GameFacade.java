package com.bol.game.facade;

import com.bol.game.dto.request.CreateGameDto;
import com.bol.game.dto.request.RequestTurnDto;
import com.bol.game.dto.response.GameDto;

import java.util.UUID;

public interface GameFacade {

    GameDto createGame(UUID userId, CreateGameDto body);

    GameDto joinGame(UUID userId, UUID gameId);

    void requestTurn(UUID gameId, RequestTurnDto message);
}
