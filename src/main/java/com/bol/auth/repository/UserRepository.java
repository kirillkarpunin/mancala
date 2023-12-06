package com.bol.auth.repository;

import com.bol.auth.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    boolean existsById(UUID userId);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    User save(User user);
}
