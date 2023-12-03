package com.bol.controller;

import com.bol.dto.request.CreateGameDto;
import com.bol.dto.request.JoinGameDto;
import com.bol.dto.response.GameDto;
import com.bol.engine.model.GameConfiguration;
import com.bol.service.GameService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// TODO: Consider interface
// TODO: Swagger
@RequestMapping("/v1/games")
@RestController
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public GameDto create(@RequestBody CreateGameDto body) {
        return toDto(gameService.createGame(body));
    }

    @PostMapping("/{gameId}/join")
    public GameDto join(@PathVariable UUID gameId, @RequestBody JoinGameDto body) {
        return toDto(gameService.joinGame(gameId, body));
    }

    private static GameDto toDto(GameConfiguration gameConfiguration) {
        return new GameDto(gameConfiguration.getId(), gameConfiguration.getStatus());
    }
}
