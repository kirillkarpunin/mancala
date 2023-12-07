package com.bol.game.api;

import com.bol.game.dto.request.CreateGameDto;
import com.bol.game.dto.response.GameDto;
import com.bol.security.jwt.JwtAuthenticationToken;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Validated
@RequestMapping("/api/v1/games")
public interface GameRestApi {

    @Valid
    @PostMapping
    GameDto create(@Valid @RequestBody CreateGameDto body, JwtAuthenticationToken authentication);

    @Valid
    @PostMapping("/{gameId}/join")
    GameDto join(@PathVariable UUID gameId, JwtAuthenticationToken authentication);
}