package com.bol.game.api;

import com.bol.game.dto.request.CreateGameDto;
import com.bol.game.dto.response.GameDto;
import com.bol.security.jwt.JwtAuthenticationToken;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Create new game")
    @Valid
    @PostMapping
    GameDto create(@Valid @RequestBody CreateGameDto body, JwtAuthenticationToken authentication);

    @Operation(summary = "Join to existing game")
    @Valid
    @PostMapping("/{gameId}/join")
    GameDto join(@PathVariable UUID gameId, JwtAuthenticationToken authentication);
}
