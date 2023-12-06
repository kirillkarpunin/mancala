package com.bol.auth.dto.response;

import java.util.UUID;

public record UserDto(UUID id, String name, String token) {
}
