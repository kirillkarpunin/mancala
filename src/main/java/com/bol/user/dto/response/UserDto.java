package com.bol.user.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserDto(
        @NotNull UUID id,
        @NotBlank String name,
        @NotBlank String token
) {
}
