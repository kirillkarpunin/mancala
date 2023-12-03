package com.bol.controller;

import com.bol.dto.request.CreateGameDto;
import com.bol.dto.response.GameDto;
import com.bol.engine.model.GameConfiguration;
import com.bol.service.GameService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private static GameDto toDto(GameConfiguration gameConfiguration) {
        return new GameDto(gameConfiguration.getId());
    }
}
