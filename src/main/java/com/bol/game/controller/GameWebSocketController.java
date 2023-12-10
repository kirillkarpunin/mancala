package com.bol.game.controller;

import com.bol.game.api.GameWebSocketApi;
import com.bol.game.dto.request.RequestTurnDto;
import com.bol.game.facade.GameFacade;
import com.bol.security.jwt.JwtAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class GameWebSocketController implements GameWebSocketApi {

    private final GameFacade gameFacade;

    @Override
    public void requestTurn(UUID gameId, RequestTurnDto body, JwtAuthenticationToken authentication) {
        var userId = authentication.getUserId();
        gameFacade.requestTurn(userId, gameId, body);
    }
}
