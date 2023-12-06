package com.bol.auth.repository;

import com.bol.auth.model.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Repository
public class InMemoryUserRepository implements UserRepository {

    // TODO: Concurrency support
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public boolean existsById(UUID userId) {
        assert userId != null;
        return findBy(user -> user.id().equals(userId)).isPresent();
    }

    @Override
    public boolean existsByUsername(String username) {
        assert username != null;
        return findByUsername(username).isPresent();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        assert username != null;
        return findBy(user -> user.name().equals(username));
    }

    @Override
    public User save(User user) {
        assert user != null;
        users.put(user.id(), user);
        return user;
    }

    private Optional<User> findBy(Predicate<User> predicate) {
        return users.values().stream()
                .filter(predicate)
                .findFirst();
    }
}
