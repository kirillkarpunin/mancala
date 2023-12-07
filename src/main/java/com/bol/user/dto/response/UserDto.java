package com.bol.user.dto.response;

import java.util.UUID;

public record UserDto(UUID id, String name, String token) {
}
