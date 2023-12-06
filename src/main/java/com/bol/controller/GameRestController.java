package com.bol.controller;

import com.bol.auth.configuration.converter.JwtAuthenticationToken;
import com.bol.dto.request.CreateGameDto;
import com.bol.dto.response.GameDto;
import com.bol.service.GameService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// TODO: Consider interface
// TODO: Swagger
@RestController
@RequestMapping("/api/v1/games")
public class GameRestController {

    private final GameService gameService;

    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public GameDto create(@RequestBody CreateGameDto body, JwtAuthenticationToken authentication) {
        var userId = authentication.getUserId();
        return gameService.createGame(userId, body);
    }

    @PostMapping("/{gameId}/join")
    public GameDto join(@PathVariable UUID gameId, JwtAuthenticationToken authentication) {
        var userId = authentication.getUserId();
        return gameService.joinGame(userId, gameId);
    }
}
