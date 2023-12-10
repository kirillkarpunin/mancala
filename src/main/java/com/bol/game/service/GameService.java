package com.bol.game.service;

import com.bol.game.Game;
import com.bol.game.dto.request.CreateGameDto;
import com.bol.game.dto.request.RequestTurnDto;

import java.util.UUID;

public interface GameService {

    Game createGame(UUID userId, CreateGameDto body);

    Game joinGame(UUID userId, UUID gameId);

    Game requestTurn(UUID userId, UUID gameId, RequestTurnDto body);
}
