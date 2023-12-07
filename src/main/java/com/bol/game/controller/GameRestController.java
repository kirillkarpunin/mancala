package com.bol.game.controller;

import com.bol.game.api.GameRestApi;
import com.bol.game.dto.request.CreateGameDto;
import com.bol.game.dto.response.GameDto;
import com.bol.game.service.GameService;
import com.bol.security.jwt.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@CrossOrigin
@RestController
public class GameRestController implements GameRestApi {

    private final GameService gameService;

    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public GameDto create(CreateGameDto body, JwtAuthenticationToken authentication) {
        var userId = authentication.getUserId();
        return gameService.createGame(userId, body);
    }

    @Override
    public GameDto join(UUID gameId, JwtAuthenticationToken authentication) {
        var userId = authentication.getUserId();
        return gameService.joinGame(userId, gameId);
    }
}
