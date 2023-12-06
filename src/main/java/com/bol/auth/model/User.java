package com.bol.auth.model;

import java.util.UUID;

public record User(UUID id, String name, String password) {
}
