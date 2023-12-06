package com.bol.controller;

import com.bol.dto.request.RequestTurnDto;
import com.bol.dto.response.GameDto;
import com.bol.service.GameService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class GameWebSocketController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameWebSocketController(GameService gameService, SimpMessagingTemplate simpMessagingTemplate) {
        this.gameService = gameService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/game.{gameId}")
    public GameDto requestTurn(@DestinationVariable UUID gameId, @Payload RequestTurnDto payload) {
        var game = gameService.requestTurn(gameId, payload);
        simpMessagingTemplate.convertAndSend("/topic/game-state.%s".formatted(gameId), game);

        return game;
    }
}
