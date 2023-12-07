package com.bol.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterDto(
        @NotBlank String username,
        @NotBlank String password
) {
}
