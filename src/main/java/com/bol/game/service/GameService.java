package com.bol.game.service;

import com.bol.game.dto.request.CreateGameDto;
import com.bol.game.dto.request.RequestTurnDto;
import com.bol.game.dto.response.GameDto;

import java.util.UUID;

public interface GameService {

    GameDto createGame(UUID userId, CreateGameDto body);

    GameDto joinGame(UUID userId, UUID gameId);

    GameDto requestTurn(UUID gameId, RequestTurnDto message);
}
