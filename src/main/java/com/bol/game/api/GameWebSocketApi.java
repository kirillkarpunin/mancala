package com.bol.game.api;

import com.bol.game.dto.request.RequestTurnDto;
import com.bol.security.jwt.JwtAuthenticationToken;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.UUID;

public interface GameWebSocketApi {
    @MessageMapping("/game.{gameId}")
    void requestTurn(
            @DestinationVariable UUID gameId,
            @Valid @Payload RequestTurnDto body,
            JwtAuthenticationToken authentication
    );

}
