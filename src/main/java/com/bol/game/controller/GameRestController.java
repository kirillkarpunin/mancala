package com.bol.game.controller;

import com.bol.game.api.GameRestApi;
import com.bol.game.dto.request.CreateGameDto;
import com.bol.game.dto.response.GameDto;
import com.bol.game.facade.GameFacade;
import com.bol.security.jwt.JwtAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class GameRestController implements GameRestApi {

    private final GameFacade gameFacade;

    @Override
    public GameDto create(CreateGameDto body, JwtAuthenticationToken authentication) {
        var userId = authentication.getUserId();
        return gameFacade.createGame(userId, body);
    }

    @Override
    public GameDto join(UUID gameId, JwtAuthenticationToken authentication) {
        var userId = authentication.getUserId();
        return gameFacade.joinGame(userId, gameId);
    }
}
