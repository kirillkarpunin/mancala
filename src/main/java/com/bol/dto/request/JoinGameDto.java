package com.bol.dto.request;

import java.util.UUID;

// TODO: Enable Spring Security and get userId from token
public record JoinGameDto(
     UUID userId
) {
}
