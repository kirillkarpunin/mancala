package com.bol.auth.repository;

import com.bol.auth.model.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryUserRepository implements UserRepository {

    // TODO: Concurrency support
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.name().equals(username))
                .findFirst();
    }

    @Override
    public User save(User user) {
        users.put(user.id(), user);
        return user;
    }
}
