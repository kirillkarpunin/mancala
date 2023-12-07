package com.bol.user.model;

import java.util.UUID;

public record User(UUID id, String name, String password) {
}
