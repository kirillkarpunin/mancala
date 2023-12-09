package com.bol.game.controller;

import com.bol.game.dto.request.RequestTurnDto;
import com.bol.game.facade.GameFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final GameFacade gameFacade;

    @MessageMapping("/game.{gameId}")
    public void requestTurn(@DestinationVariable UUID gameId, @Payload RequestTurnDto body) {
        gameFacade.requestTurn(gameId, body);
    }
}
